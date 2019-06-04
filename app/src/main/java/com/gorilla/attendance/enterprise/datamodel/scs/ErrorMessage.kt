package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class ErrorMessage {
    @SerializedName("code")
    var code: String? = null

    @SerializedName("message")
    var message: String? = null
}