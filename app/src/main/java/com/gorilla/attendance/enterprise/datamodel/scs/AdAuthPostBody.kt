package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class AdAuthPostBody {
    @SerializedName("username")
    var username: String? = null

    @SerializedName("password")
    var password: String? = null
}