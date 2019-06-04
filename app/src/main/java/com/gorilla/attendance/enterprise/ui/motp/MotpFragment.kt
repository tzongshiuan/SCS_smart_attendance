package com.gorilla.attendance.enterprise.ui.motp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context.LOCATION_SERVICE
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gorilla.attendance.enterprise.R
import com.gorilla.attendance.enterprise.databinding.MotpFragmentBinding
import com.gorilla.attendance.enterprise.util.Constants
import com.gorilla.attendance.enterprise.util.EnterpriseUtils
import com.gorilla.attendance.enterprise.util.LOG
import com.gorilla.attendance.enterprise.util.scs.FDRControlManager
import kotlinx.android.synthetic.main.motp_fragment.*
import java.lang.ref.WeakReference

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/21
 */
class MotpFragment : Fragment() {

    companion object {
        private val TAG = MotpFragment::class.java.simpleName

        class MotpFragmentHandler(fragment: MotpFragment) : Handler() {

            private var mFragment: WeakReference<MotpFragment>? = null

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
                        fragment?.onGetFaceTooLong()
                    }
                }
            }
        }
    }

    private lateinit var motpViewModel: MotpViewModel

    private lateinit var mBinding: MotpFragmentBinding

    private val mFragmentHandler = MotpFragmentHandler(this)

    private var mIsStartFdr = false

    private var mLastClickBtnTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.D(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        LOG.D(TAG, "onCreateView()")

        mBinding = MotpFragmentBinding.inflate(inflater, container, false)

        initUI()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LOG.D(TAG, "onActivityCreated()")

        motpViewModel = ViewModelProviders.of(this).get(MotpViewModel::class.java)
        mBinding.motpViewModel = motpViewModel

        motpViewModel.initSetting(activity)

        motpViewModel.uiModeLiveData.observe(this, Observer {
            mBinding.fdrFrame.visibility = View.GONE
            mBinding.faceImage.visibility = View.GONE
            mBinding.fdrTextView.visibility = View.GONE
            mBinding.groupEnterPin.visibility = View.GONE
            mBinding.groupUnauthorized.visibility = View.GONE
            mBinding.groupCantDetect.visibility = View.GONE
            mBinding.groupMotpSuccess.visibility = View.GONE
            mBinding.groupFaceImage.visibility = View.GONE

            when (it) {
                MotpViewModel.UI_ENTER_PIN_CODE -> {
                    LOG.D(TAG, "change UI to enter pin code")
                    mBinding.titleTextView.title = getString(R.string.txt_get_motp)
                    mBinding.titleTextView.subTitle = getString(R.string.txt_get_motp_desc)
                    mBinding.groupEnterPin.visibility = View.VISIBLE
                }

                MotpViewModel.UI_FACE_DETECTING -> {
                    LOG.D(TAG, "change UI to face detecting mode")

                    mBinding.pinEditText.text.clear()

                    fdrFrame.visibility = View.VISIBLE
                    mBinding.fdrTextView.visibility = View.VISIBLE
                    startFdr()
                }

                MotpViewModel.UI_UNAUTHORIZED_USER -> {
                    LOG.D(TAG, "change UI to show that unauthorized user result")
                    mBinding.titleTextView.title = getString(R.string.txt_face_login)
                    mBinding.titleTextView.subTitle = getString(R.string.txt_recognition_success)
                    mBinding.failDescTextView.text = getString(R.string.txt_please_try_again)
                    mBinding.faceImage.visibility = View.VISIBLE
                    mBinding.groupFaceImage.visibility = View.VISIBLE
                    mBinding.groupUnauthorized.visibility = View.VISIBLE
                }

                MotpViewModel.UI_CAN_NOT_DETECT_FACE -> {
                    LOG.D(TAG, "change UI to show that app can not detect face")
                    mBinding.titleTextView.title = getString(R.string.txt_face_login)
                    mBinding.titleTextView.subTitle = getString(R.string.txt_recognition_success)
                    mBinding.failDescTextView.text = getString(R.string.txt_could_not_detect)
                    mBinding.groupFaceImage.visibility = View.VISIBLE
                    mBinding.groupCantDetect.visibility = View.VISIBLE
                }

                MotpViewModel.UI_GET_MOTP_SUCCESS -> {
                    LOG.D(TAG, "change UI to show MOTP result")
                    mBinding.titleTextView.title = getString(R.string.txt_identity_confirm)
                    mBinding.titleTextView.subTitle = getString(R.string.txt_recognition_success)
                    mBinding.groupMotpSuccess.visibility = View.VISIBLE
                }

                else -> {}
            }
        })

        motpViewModel.errorLiveData.observe(this, Observer {
            Toast.makeText(this.context, it?.message, Toast.LENGTH_SHORT).show()
        })

        motpViewModel.motpLiveData.observe(this, Observer { motp->
            motp?.let { mBinding.motpResultTextView.text = it }
        })
    }

    override fun onResume() {
        LOG.D(TAG, "onResume()")
        super.onResume()
    }

    override fun onPause() {
        LOG.D(TAG, "onPause()")
        super.onPause()
    }

    override fun onStop() {
        LOG.D(TAG, "onStop()")
        super.onStop()
    }

    override fun onDestroy() {
        LOG.D(TAG, "onDestroy()")
        super.onDestroy()
    }

    private fun initUI() {
        LOG.D(TAG, "initUI()")

        mBinding.confirmPinCodeBtn.setOnClickListener {
            if ((System.currentTimeMillis() - mLastClickBtnTime) > 1000) {
                mLastClickBtnTime = System.currentTimeMillis()

                motpViewModel.confirmPinCode(context)
            }
        }

        mBinding.createAdBtn.setOnClickListener {
            motpViewModel.createAdAccount(mBinding.root)
        }

        mBinding.tryAgainShortBtn.setOnClickListener {
            motpViewModel.uiModeLiveData.value = MotpViewModel.UI_FACE_DETECTING
        }

        mBinding.tryAgainLongBtn.setOnClickListener {
            motpViewModel.uiModeLiveData.value = MotpViewModel.UI_FACE_DETECTING
        }

        mBinding.doneBtn.setOnClickListener {
            if ((System.currentTimeMillis() - mLastClickBtnTime) > 1000) {
                mLastClickBtnTime = System.currentTimeMillis()

                motpViewModel.done()
            }
        }
    }

    private fun startFdr() {
        if (mIsStartFdr) {
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

    private fun stopFdr() {
        mIsStartFdr = false
        mBinding.fdrFrame.removeAllViews()
        FDRControlManager.getInstance(context).stopFdr(mFragmentHandler)
        mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG)
    }

    private fun setFaceImage() {
        stopFdr()

        if (EnterpriseUtils.mFacePngList != null && EnterpriseUtils.mFacePngList.size > 0) {
            LOG.D(TAG, "mFacePngList size = ${EnterpriseUtils.mFacePngList.size}")
            val bitmap = BitmapFactory.decodeByteArray(EnterpriseUtils.mFacePngList[0], 0, EnterpriseUtils.mFacePngList[0].size)
            mBinding.faceImage.setImageBitmap(bitmap)
        } else {
            LOG.E(TAG, "mFacePngList is null or empty")
        }

        mBinding.fdrFrame.visibility = View.GONE
        mBinding.faceImage.visibility = View.VISIBLE
    }

    private fun onGetFaceSuccess() {
        setFaceImage()
        mBinding.faceImage.setBackgroundColor(Color.TRANSPARENT)
        motpViewModel.verifyFaceData(EnterpriseUtils.mFacePngList[0])
    }

    private fun onGetFaceFailed() {
        LOG.E(TAG, "should not happen here now")
        assert(false)
        setFaceImage()
    }

    private fun onGetFaceTooLong() {
        motpViewModel.onReceiveFdrResult(MotpViewModel.FDR_CANT_DETECT_FACE)
        mBinding.faceImage.setBackgroundColor(Color.BLACK)
    }
}
