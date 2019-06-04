package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class ScsAuthResponse {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("data")
    var data: ResponseData? = null

    @SerializedName("error")
    var error: ErrorMessage? = null
}