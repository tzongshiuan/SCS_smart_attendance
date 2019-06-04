package com.gorilla.attendance.enterprise.datamodel.scs

import com.google.gson.annotations.SerializedName

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/3/4
 * Description:
 */
class ScsUnrecognizePostBody {
    @SerializedName("errorLogs")
    var errorLogs = ArrayList<ScsErrorLog>()
}