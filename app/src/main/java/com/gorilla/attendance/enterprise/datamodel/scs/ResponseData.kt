package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/3/4
 * Description:
 */
class ResponseData {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("loginMode")
    var loginMode: String? = null
}