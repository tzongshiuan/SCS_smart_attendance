package com.gorilla.attendance.enterprise.ui.register

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.gorilla.attendance.enterprise.R
import com.gorilla.attendance.enterprise.databinding.PinRegisterFragmentBinding
import com.gorilla.attendance.enterprise.datamodel.scs.LoginResult
import com.gorilla.attendance.enterprise.datamodel.scs.PinRegisterResult
import com.gorilla.attendance.enterprise.util.KeyboardManager
import com.gorilla.attendance.enterprise.util.LOG
import kotlinx.android.synthetic.main.pin_register_fragment.view.*

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/19
 */
class PinRegisterFragment : Fragment() {

    companion object {
        private val TAG = PinRegisterFragment::class.java.simpleName
    }

    private lateinit var registerViewModel: PinRegisterViewModel

    private lateinit var mBinding: PinRegisterFragmentBinding

    private var mLastRegisterTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.D(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        LOG.D(TAG, "onCreateView()")

        mBinding = PinRegisterFragmentBinding.inflate(inflater, container, false)

        initUI()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LOG.D(TAG, "onActivityCreated()")

        registerViewModel = ViewModelProviders.of(this).get(PinRegisterViewModel::class.java)
        mBinding.registerViewModel = registerViewModel

        //registerViewModel.pinCode = "pin"
        //registerViewModel.emergencyCode = "emergency"

        registerViewModel.registerResultLiveData.observe(this, Observer {
            when (it) {
                PinRegisterResult.EMPTY_PIN_CODE -> {
                    LOG.D(TAG, "pin code is empty")
                    Toast.makeText(this.context, R.string.msg_pin_code_empty, Toast.LENGTH_SHORT).show()

                    KeyboardManager.showSoftKeyboard(context, mBinding.pinEditText)
                }

                PinRegisterResult.EMPTY_EMERGENCY_CODE -> {
                    LOG.D(TAG, "emergency code is empty")
                    Toast.makeText(this.context, R.string.msg_emergency_code_empty, Toast.LENGTH_SHORT).show()

                    KeyboardManager.showSoftKeyboard(context, mBinding.emergencyEditText)
                }

                PinRegisterResult.DUPLICATE_PIN_CODE -> {
                    LOG.D(TAG, "pin code is the same as emergency code")
                    Toast.makeText(context, R.string.msg_duplicate_pin_code, Toast.LENGTH_SHORT).show()
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

        mBinding.registerBtn.setOnClickListener {
            if ((System.currentTimeMillis() - mLastRegisterTime) > 1000) {
                mLastRegisterTime = System.currentTimeMillis()

                registerViewModel.registerPinCode(mBinding.root)
            }
        }
    }
}
