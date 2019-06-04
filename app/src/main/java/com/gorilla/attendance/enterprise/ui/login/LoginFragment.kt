package com.gorilla.attendance.enterprise.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gorilla.attendance.enterprise.R
import com.gorilla.attendance.enterprise.databinding.LoginFragmentBinding
import com.gorilla.attendance.enterprise.datamodel.scs.ErrorMessageTable
import com.gorilla.attendance.enterprise.datamodel.scs.LoginResult
import com.gorilla.attendance.enterprise.util.LOG
import com.gorilla.attendance.enterprise.util.KeyboardManager
import com.gorilla.attendance.enterprise.util.scs.DebugManager


/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/19
 */
class LoginFragment : Fragment() {

    companion object {
        private val TAG = LoginFragment::class.java.simpleName
    }

    private lateinit var loginViewModel: LoginViewModel

    private lateinit var mBinding: LoginFragmentBinding

    private var mLastLoginTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.D(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        LOG.D(TAG, "onCreateView()")

        mBinding = LoginFragmentBinding.inflate(inflater, container, false)

        initUI()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LOG.D(TAG, "onActivityCreated()")

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        mBinding.loginViewModel = loginViewModel

        loginViewModel.adAccount = DebugManager.DEFAULT_AD_ACCOUNT
        loginViewModel.adPassword = DebugManager.DEFAULT_AD_PASSWORD

        loginViewModel.loginResultLiveData.observe(this, Observer {
            when (it) {
                LoginResult.EMPTY_AD_ACCOUNT -> {
                    LOG.D(TAG, "ad account is empty")
                    Toast.makeText(this.context, R.string.msg_ad_account_empty, Toast.LENGTH_SHORT).show()
                    KeyboardManager.showSoftKeyboard(context, mBinding.adEditText)
                }

                LoginResult.EMPTY_AD_PASSWORD -> {
                    LOG.D(TAG, "ad password is empty")
                    Toast.makeText(this.context, R.string.msg_ad_password_empty, Toast.LENGTH_SHORT).show()
                    KeyboardManager.showSoftKeyboard(context, mBinding.pwEditText)
                }

                LoginResult.WRONG_AD_INFO -> {
                    LOG.D(TAG, getString(R.string.error_user_forbidden_2))
                    Toast.makeText(this.context, R.string.msg_wong_ad_info, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
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

        mBinding.loginBtn.setOnClickListener {
            if ((System.currentTimeMillis() - mLastLoginTime) > 1000) {
                mLastLoginTime = System.currentTimeMillis()

                loginViewModel.login(mBinding.root)
            }
        }
    }
}
