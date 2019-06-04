package com.gorilla.attendance.enterprise.util.scs

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/26
 * Description:
 */
class DebugManager {
    companion object {
        var DEBUG_MODE = false

        // LoginFragment
        const val FORCE_AD_AUTH_SUCCESS = false

        // PinRegisterFragment
        const val FORCE_PIN_CODE_SUCCESS = false

        // EnrollFragment
        const val SKIP_FACE_DETECTING = false
        const val FORCE_CONFIRM_SUCCESS = false

        // MotpFragment
        const val FORCE_SA_AUTH_SUCCESS = false
        const val SKIP_FACE_VERIFY = false
        const val FORCE_VERIFY_SUCCESS = false
        const val SKIP_SEND_RECORD = false

        const val DEFAULT_AD_ACCOUNT = "Administrator"
        const val DEFAULT_AD_PASSWORD = "Gorilla@"
        const val DEFAULT_PIN_CODE = "a07121234"
        const val DEFAULT_EMERGENCY_CODE = "Hsuan6391"
    }
}