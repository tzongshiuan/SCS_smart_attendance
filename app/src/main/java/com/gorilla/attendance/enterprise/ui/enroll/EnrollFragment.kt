package com.gorilla.attendance.enterprise.ui.enroll

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gorilla.attendance.enterprise.R
import com.gorilla.attendance.enterprise.databinding.EnrollFragmentBinding
import com.gorilla.attendance.enterprise.datamodel.scs.ScsPreferences
import com.gorilla.attendance.enterprise.util.Constants
import com.gorilla.attendance.enterprise.util.EnterpriseUtils
import com.gorilla.attendance.enterprise.util.LOG
import com.gorilla.attendance.enterprise.util.scs.DebugManager
import com.gorilla.attendance.enterprise.util.scs.FDRControlManager
import java.lang.ref.WeakReference

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/20
 */
class EnrollFragment : Fragment() {

    companion object {
        private val TAG = EnrollFragment::class.java.simpleName

        //private const val TAKE_PHOTO_REQUEST = 101

        class EnrollFragmentHandler(fragment: EnrollFragment) : Handler() {

            private var mFragment: WeakReference<EnrollFragment>? = null

            init {
                mFragment = WeakReference(fragment)
            }

            override fun handleMessage(msg: Message) {
                LOG.D(TAG, "mHandler msg.what = " + msg.what)

                val fragment = mFragment?.get()
                fragment?.mIsStartFdr = false

                when (msg.what) {
                    Constants.GET_FACE_SUCCESS -> {
                        this.removeMessages(Constants.GET_FACE_TOO_LONG)
                        fragment?.onGetFaceSuccess()
                    }

                    Constants.GET_FACE_FAIL -> {
                        this.removeMessages(Constants.GET_FACE_TOO_LONG)
                        fragment?.onGetFaceFailed()
                    }

                    Constants.GET_FACE_TOO_LONG -> {
                    }
                }
            }
        }
    }

    private lateinit var enrollViewModel: EnrollViewModel

    private lateinit var mBinding: EnrollFragmentBinding

    //private var mCurrentPhotoPath: String = ""

    private var mLastClickBtnTime = 0L

    private var mIsStartFdr = false
    private var mIsGotFacePic = false

    private val mFragmentHandler = EnrollFragmentHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.D(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        LOG.D(TAG, "onCreateView()")

        mBinding = EnrollFragmentBinding.inflate(inflater, container, false)

        initUI()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LOG.D(TAG, "onActivityCreated()")

        enrollViewModel = ViewModelProviders.of(this).get(EnrollViewModel::class.java)
        mBinding.enrollViewModel = enrollViewModel

        enrollViewModel.pinCode = arguments?.getString("pinCode")
        enrollViewModel.emergencyCode = arguments?.getString("emergencyCode")
        LOG.D(TAG, "get arguments, pinCode: ${enrollViewModel.pinCode}, emergencyCode: ${enrollViewModel.emergencyCode}")

        enrollViewModel.isConfirmPhotoLiveData.observe(this, Observer {
            if (it!!) {
                // show UI after confirm photo
                mBinding.group1.visibility = View.GONE
                mBinding.group2.visibility = View.VISIBLE
            } else {
                // show UI before confirm photo
                mBinding.group1.visibility = View.VISIBLE
                mBinding.group2.visibility = View.GONE
            }
        })

        enrollViewModel.errorLiveData.observe(this, Observer {
            Toast.makeText(this.context, it?.message, Toast.LENGTH_SHORT).show()
        })

        enrollViewModel.isWaitingLiveData.observe(this, Observer { isWaiting ->
            if (isWaiting!!) {
                mBinding.progress.setVisibleWithAnimate(true)
            } else {
                mBinding.progress.setVisibleImmediately(false)
            }
        })

        // preserve the method
        //launchCamera()
    }

    override fun onResume() {
        LOG.D(TAG, "onResume()")
        super.onResume()

        startFdr()
    }

    override fun onPause() {
        LOG.D(TAG, "onPause()")
        super.onPause()
    }

    override fun onStop() {
        LOG.D(TAG, "onStop()")
        super.onStop()

        stopFdr()
    }

    override fun onDestroy() {
        LOG.D(TAG, "onDestroy()")
        super.onDestroy()
    }

    private fun initUI() {
        LOG.D(TAG, "initUI()")

        mBinding.idTextView.text = ScsPreferences.getInstance().adAccount

        mBinding.takePhotoBtn.setOnClickListener {
            if ((System.currentTimeMillis() - mLastClickBtnTime) > 1000) {
                mLastClickBtnTime = System.currentTimeMillis()

                if (!mIsGotFacePic) {
                    Toast.makeText(context, R.string.msg_no_photo, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                mIsGotFacePic = false

                startFdr()
            }
        }

        mBinding.confirmBtn.setOnClickListener {
            if ((System.currentTimeMillis() - mLastClickBtnTime) > 1000) {
                mLastClickBtnTime = System.currentTimeMillis()

                if (DebugManager.DEBUG_MODE && DebugManager.SKIP_FACE_DETECTING) {
                    enrollViewModel.confirmPhoto()
                    return@setOnClickListener
                }

                if (!mIsGotFacePic) {
                    Toast.makeText(context, R.string.msg_no_photo, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                enrollViewModel.confirmPhoto()
            }
        }

        mBinding.getMotpBtn.setOnClickListener {
            if ((System.currentTimeMillis() - mLastClickBtnTime) > 1000) {
                mLastClickBtnTime = System.currentTimeMillis()

                enrollViewModel.getMotp(mBinding.root)
            }
        }

        mBinding.doneBtn.setOnClickListener {
            this.activity?.finish()
        }
    }

//    private fun launchCamera() {
//        val values = ContentValues(1)
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
//        val fileUri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (intent.resolveActivity(activity!!.packageManager) != null) {
//            mCurrentPhotoPath = fileUri.toString()
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
//        }
//    }

    private fun startFdr() {
        if (mIsStartFdr || mIsGotFacePic) {
            return
        }
        mIsStartFdr = true

        if (mBinding.fdrFrame.childCount == 0) {
            mBinding.fdrFrame.addView(FDRControlManager.getInstance(context).fdrCtrl)
        }
        FDRControlManager.getInstance(context).startFdr(mFragmentHandler)

        mBinding.faceImage.setImageBitmap(null)
        mBinding.fdrFrame.visibility = View.VISIBLE
        mBinding.faceImage.visibility = View.GONE
    }

    private fun onGetFaceSuccess() {
        if (EnterpriseUtils.mFacePngList != null && EnterpriseUtils.mFacePngList.size > 0) {
            mIsGotFacePic = true
            stopFdr()

            LOG.D(TAG, "mFacePngList size = ${EnterpriseUtils.mFacePngList.size}")
            val bitmap = BitmapFactory.decodeByteArray(EnterpriseUtils.mFacePngList[0], 0, EnterpriseUtils.mFacePngList[0].size)
            mBinding.faceImage.setImageBitmap(bitmap)
            mBinding.thumbImage.setImageBitmap(bitmap)

            mBinding.fdrFrame.visibility = View.GONE
            mBinding.faceImage.visibility = View.VISIBLE
        } else {
            LOG.E(TAG, "mFacePngList is null or empty")
        }
    }

    private fun onGetFaceFailed() {
    }

    private fun stopFdr() {
        mIsStartFdr = false
        mBinding.fdrFrame.removeAllViews()
        FDRControlManager.getInstance(context).stopFdr(mFragmentHandler)
        mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG)
    }
}
