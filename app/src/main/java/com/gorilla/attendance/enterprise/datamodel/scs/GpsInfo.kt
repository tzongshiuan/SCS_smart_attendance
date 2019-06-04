package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class GpsInfo {
    @SerializedName("longitude")
    var longitude: String? = null

    @SerializedName("latitude")
    var latitude: String? = null

    @SerializedName("altitude")
    var altitude: String? = null

    fun toJson(): JSONObject {
        val jObj = JSONObject()
        jObj.put("longitude", longitude)
        jObj.put("latitude", latitude)
        jObj.put("altitude", altitude)
        return jObj
    }
}