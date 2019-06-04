package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/27
 * Description:
 */
class ScsErrorLog {
    @SerializedName("serial")
    var serial: Int? = null

    @SerializedName("deviceTime")
    var deviceTime: String? = null

    @SerializedName("faceImg")
    var faceImg: String? = null

    @SerializedName("username")
    var username: String? = null

    @SerializedName("mobileUid")
    var mobileUid: String? = null

    @SerializedName("pinCode")
    var pinCode: String? = null

    @SerializedName("gps")
    var gps: GpsInfo? = null

    fun toJson(): JSONObject {
        val jObj = JSONObject()
        jObj.put("serial", serial)
        jObj.put("deviceTime", deviceTime)
        jObj.put("faceImg", faceImg)
        jObj.put("username", username)
        jObj.put("mobileUid", mobileUid)
        jObj.put("pinCode", pinCode)
        jObj.put("gps", gps?.toJson())
        return jObj
    }
}