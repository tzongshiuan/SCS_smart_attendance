package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class ScsBapVerifyPostBody {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("imageList")
    var imageList: ArrayList<FaceImage> = ArrayList()
}