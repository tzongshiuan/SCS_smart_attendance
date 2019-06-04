package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class FaceImage {
    @SerializedName("format")
    var format: String? = null

    @SerializedName("dataInBase64")
    var dataInBase64: String? = null
}