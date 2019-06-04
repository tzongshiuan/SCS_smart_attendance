package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class SaAuthPostBody {
    @SerializedName("mobileUid")
    var mobileUid: String? = null

    @SerializedName("username")
    var username: String? = null

    @SerializedName("pinCode")
    var pinCode: String? = null
}