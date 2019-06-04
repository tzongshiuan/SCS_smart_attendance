package com.gorilla.attendance.enterprise.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.android.vending.expansion.downloader.DownloadProgressInfo
import com.gorilla.attendance.enterprise.R
import com.gorilla.attendance.enterprise.databinding.ScsMainFragmentBinding
import com.gorilla.attendance.enterprise.datamodel.scs.ScsPreferences
import com.gorilla.attendance.enterprise.util.EnterpriseUtils
import com.gorilla.attendance.enterprise.util.scs.FDRControlManager
import com.gorilla.attendance.enterprise.util.LOG
import com.gorilla.attendance.enterprise.util.SimpleDelayTask
import com.gorilla.attendance.enterprise.util.scs.Constants.SPLASH_DELAY_TIME
import com.gorilla.enroll.lib.listener.ObbFileListener
import com.gorilla.enroll.lib.util.EnrollUtil
import com.gorilla.enroll.lib.util.ObbFileManager
import android.app.AlertDialog
import android.os.Build.VERSION_CODES.O
import android.provider.ContactsContract
import android.support.v4.content.ContextCompat
import com.gorilla.attendance.enterprise.api.ImpApiService
import com.tsunghsuanparty.textanimlib.slide.SlideAnimation
import kotlinx.android.synthetic.main.set_domain_dialog.view.*


/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/20
 */
class ScsMainFragment : Fragment() {

    companion object {
        private val TAG = ScsMainFragment::class.java.simpleName
    }

    private lateinit var mBinding: ScsMainFragmentBinding

    private lateinit var mObbFileManager: ObbFileManager

    private var isObbReady = false

    private var navLock = false

    private var isSetDomain = false

    private var setDomainDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.D(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        LOG.D(TAG, "onCreateView()")

        mBinding = ScsMainFragmentBinding.inflate(inflater, container, false)

        initUI()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LOG.D(TAG, "onActivityCreated()")

        FDRControlManager.getInstance(context, activity).initFdr()

        EnrollUtil.init(context)
        mObbFileManager = ObbFileManager.Builder(activity)
                .setPublicKey(EnterpriseUtils.PUBLIC_KEY)
                .setAPKS(EnterpriseUtils.xAPKS)
                .setFDRSoFileLength(223159052L)
                .setListener(object: ObbFileListener {
                    override fun onDownLoadState(p0: Int) {
                        LOG.D(TAG, "onDownLoadState, p0: $p0")
                    }

                    override fun onProgress(p0: DownloadProgressInfo?) {
                        LOG.D(TAG, "onProgress, p0: $p0")

                        isObbReady = false
                    }

                    override fun onState(p0: Int) {
                        LOG.D(TAG, "onState, p0: $p0")

                        if (p0 == ObbFileManager.OBB_STATE_CHECK_SO) {
                            isObbReady = true
                        }
                    }
                })
                .build()
        mObbFileManager.start()
    }

    override fun onResume() {
        LOG.D(TAG, "onResume()")
        super.onResume()

        splashRunnable()
    }

    override fun onPause() {
        LOG.D(TAG, "onPause()")
        super.onPause()
    }

    override fun onStop() {
        LOG.D(TAG, "onStop()")
        super.onStop()

        dismissSetDomainDialog()
    }

    override fun onDestroy() {
        LOG.D(TAG, "onDestroy()")
        super.onDestroy()
    }

    private fun initUI() {
        val slideAnimation = SlideAnimation()
        slideAnimation.initSettings(ContextCompat.getColor(context!!, R.color.white)
                                    , ContextCompat.getColor(context!!, R.color.button_orange)
                                    , SlideAnimation.ANIM_FAST)
        slideAnimation.startAnimation(mBinding.msgTextView)

        mBinding.domainBtn.setOnClickListener {
            showSetDomainDialog()
        }
    }

    private fun initSetDomainDialog() {
        val view = View.inflate(context, R.layout.set_domain_dialog, null)

        val builder = AlertDialog.Builder(context)
                .setCancelable(false)
                //.setTitle(R.string.txt_domain_set)
                .setView(view)

        view.domainEditText.setText(ImpApiService.SERVER_IP)

        view.checkBtn.setOnClickListener {
            ImpApiService.SERVER_IP = view.domainEditText.text.toString()
            dismissSetDomainDialog()
        }

        setDomainDialog = builder.create()
    }

    private fun showSetDomainDialog() {
        initSetDomainDialog()

        if (!setDomainDialog?.isShowing!!) {
            isSetDomain = true
            setDomainDialog?.show()
        }
    }

    private fun dismissSetDomainDialog() {
        if (setDomainDialog != null && setDomainDialog?.isShowing!!) {
            isSetDomain = false
            setDomainDialog?.dismiss()
        }
    }

    @Synchronized
    private fun splashRunnable() {
        if (navLock) {
            return
        }
        navLock = true

        SimpleDelayTask.after(SPLASH_DELAY_TIME) {
            navLock = false

            if (ScsMainActivity.IS_READY && isObbReady && !isSetDomain) {
                isSetDomain = false

                // Ever registered or not
                if (ScsPreferences.getInstance().isRegister) {
                    LOG.D(TAG, "Go to get motp fragment")
                    Navigation.findNavController(mBinding.root).navigate(R.id.action_scsMainFragment_to_motpFragment)
                } else {
                    LOG.D(TAG, "Go to register fragment")
                    Navigation.findNavController(mBinding.root).navigate(R.id.action_scsMainFragment_to_loginFragment)
                }
            } else {
                // recursive waiting until initialization and permission check finished
                LOG.D(TAG, "Recursive waiting...")
                splashRunnable()
            }
        }
    }
}
