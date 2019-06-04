package com.gorilla.attendance.enterprise.ui.register

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.gorilla.attendance.enterprise.R
import com.gorilla.attendance.enterprise.datamodel.scs.PinRegisterResult
import com.gorilla.attendance.enterprise.datamodel.scs.ScsPreferences
import com.gorilla.attendance.enterprise.util.LOG
import com.gorilla.attendance.enterprise.util.scs.DebugManager

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/19
 */
class PinRegisterViewModel : ViewModel() {
    companion object {
        private val TAG = PinRegisterViewModel::class.java.simpleName
    }

    var pinCode: String? = null
    var emergencyCode: String? = null

    val registerResultLiveData = MutableLiveData<Int>()

    fun registerPinCode(view: View) {
        LOG.D(TAG, "Do login, pinCode: $pinCode, emergencyCode: $emergencyCode")

        if (DebugManager.DEBUG_MODE and DebugManager.FORCE_PIN_CODE_SUCCESS) {
            val bundle = Bundle()
            bundle.putString("pinCode", DebugManager.DEFAULT_PIN_CODE)
            bundle.putString("emergencyCode", DebugManager.DEFAULT_EMERGENCY_CODE)
            Navigation.findNavController(view).navigate(R.id.action_pinRegisterFragment_to_enrollFragment, bundle)
            return
        }

        // Handle invalid situations
        if (pinCode.isNullOrEmpty()) {
            registerResultLiveData.value = PinRegisterResult.EMPTY_PIN_CODE
            return
        }

        if (emergencyCode.isNullOrEmpty()) {
            registerResultLiveData.value = PinRegisterResult.EMPTY_EMERGENCY_CODE
            return
        }

        if (pinCode == emergencyCode) {
            registerResultLiveData.value = PinRegisterResult.DUPLICATE_PIN_CODE
            return
        }

        // Start register
        val bundle = Bundle()
        bundle.putString("pinCode", pinCode)
        bundle.putString("emergencyCode", emergencyCode)
        Navigation.findNavController(view).navigate(R.id.action_pinRegisterFragment_to_enrollFragment, bundle)
    }
}
