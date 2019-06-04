package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class RegisterPostBody {
    @SerializedName("mobileUid")
    var mobileUid: String? = null

    @SerializedName("username")
    var username: String? = null

    @SerializedName("pinCode")
    var pinCode: String? = null

    @SerializedName("emergencyCode")
    var emergencyCode: String? = null

    @SerializedName("createTime")
    var createTime: String? = null

    @SerializedName("imageList")
    var imageList: ArrayList<FaceImage> = ArrayList()
}