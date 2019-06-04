package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/27
 * Description:
 */
class ScsRecord {
    @SerializedName("serial")
    var serial: Int? = null

    @SerializedName("username")
    var username: String? = null

    @SerializedName("pinCode")
    var pinCode: String? = null

    @SerializedName("deviceTime")
    var deviceTime: String? = null

    @SerializedName("faceVerify")
    var faceVerify: String? = null

    @SerializedName("faceImg")
    var faceImg: String? = null

    @SerializedName("mobileUid")
    var mobileUid: String? = null

    @SerializedName("loginMode")
    var loginMode: String? = null

    @SerializedName("gps")
    var gps: GpsInfo? = null

    fun toJson(): JSONObject {
        val jObj = JSONObject()
        jObj.put("serial", serial)
        jObj.put("username", username)
        jObj.put("pinCode", pinCode)
        jObj.put("deviceTime", deviceTime)
        jObj.put("faceVerify", faceVerify)
        jObj.put("faceImg", faceImg)
        jObj.put("mobileUid", mobileUid)
        jObj.put("loginMode", loginMode)
        jObj.put("gps", gps?.toJson())
        return jObj
    }
}