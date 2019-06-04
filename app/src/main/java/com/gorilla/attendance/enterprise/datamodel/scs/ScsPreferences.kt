package com.gorilla.attendance.enterprise.datamodel.scs

import android.content.Context
import com.google.gson.Gson
import com.gorilla.attendance.enterprise.api.ImpApiService
import com.gorilla.attendance.enterprise.util.LOG
import org.json.JSONArray
import org.json.JSONObject

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description: Manage all preferences in Scs Project
 */
class ScsPreferences {

    companion object {
        private val TAG = ScsPreferences::class.java.simpleName

        private const val APP_VERSION = "version"
        private const val SCS_SETTING = "scs_setting"
        private const val MOBILE_UID  = "mobile_uid"
        private const val AD_ACCOUNT  = "account"
        private const val AD_PASSWORD = "password"
        private const val IS_REGISTER = "is_register"
        private const val NETWORK_DOMAIN = "network_domain"

        private const val RECORD_SERIAL = "record_serial"
        private const val RECORD_DATA = "record_data"
        private const val ERROR_SERIAL = "error_serial"
        private const val ERROR_DATA = "error_data"

        private var instance: ScsPreferences? = null

        fun getInstance() : ScsPreferences {
            if (instance == null) {
                instance = ScsPreferences()
            }

            return instance!!
        }
    }

    var mobileUid: String? = null
    var adAccount: String? = null
    var adPassword: String? = null
    var isRegister = false

    var scsRecordSerial = 0
    var scsRecordList = ArrayList<ScsRecord>()

    var scsErrorLogSerial = 0
    var scsErrorLogList = ArrayList<ScsErrorLog>()

    private fun packPreferencesToJson(): JSONObject {
        val jSetting = JSONObject()
        jSetting.put(MOBILE_UID, mobileUid)
        jSetting.put(AD_ACCOUNT, adAccount)
        jSetting.put(AD_PASSWORD, adPassword)
        jSetting.put(IS_REGISTER, isRegister)
        jSetting.put(NETWORK_DOMAIN, ImpApiService.SERVER_IP)
        jSetting.put(RECORD_SERIAL, scsRecordSerial)
        jSetting.put(RECORD_DATA, getRecordListJsonData())
        jSetting.put(ERROR_SERIAL, scsErrorLogSerial)
        jSetting.put(ERROR_DATA, getErrorListJsonData())

        return jSetting
    }

    private fun initPreferences() {
        mobileUid = null
        adAccount = null
        adPassword = null
        scsRecordSerial = 0
        scsRecordList.clear()
    }

    private fun getRecordListJsonData(): JSONObject {
        val jObj = JSONObject()
        val jArray = JSONArray()

        if (scsRecordList.size == 0) {
            return jObj
        }

        for (i in 0..(scsRecordList.size - 1)) {
            val jRecord = scsRecordList[i].toJson()
            jArray.put(jRecord)
        }

        jObj.put("records", jArray)
        return jObj
    }

    private fun getErrorListJsonData(): JSONObject {
        val jObj = JSONObject()
        val jArray = JSONArray()

        if (scsErrorLogList.size == 0) {
            return jObj
        }

        for (i in 0..(scsErrorLogList.size - 1)) {
            val jRecord = scsErrorLogList[i].toJson()
            jArray.put(jRecord)
        }

        jObj.put("errorLogs", jArray)
        return jObj
    }

    private fun setRecordListFromJson(jObj: JSONObject) {
        val jArray = jObj.getJSONArray("records")

        val myJsonParser = Gson()
        for (i in 0..jArray.length()) {
            val jRecord = jArray.getJSONObject(i)
            val record = myJsonParser.fromJson(jRecord.toString(), ScsRecord::class.java)
            scsRecordList.add(record)
        }
    }

    private fun setErrorListFromJson(jObj: JSONObject) {
        val jArray = jObj.getJSONArray("errorLogs")

        val myJsonParser = Gson()
        for (i in 0..jArray.length()) {
            val jRecord = jArray.getJSONObject(i)
            val record = myJsonParser.fromJson(jRecord.toString(), ScsErrorLog::class.java)
            scsErrorLogList.add(record)
        }
    }

    fun readPreferences(context: Context) {
        val preferences = context.getSharedPreferences("preference", Context.MODE_PRIVATE)

        // to check whether use the setting in the same version
        val versionName = preferences.getString(APP_VERSION, "")
        LOG.D(TAG, "versionName: $versionName")
        if (versionName != getVersionName(context)) {
            LOG.D(TAG, "Read reference failed, previous version ${getVersionName(context)}")
            initPreferences()
            return
        }

        val jSettings = JSONObject(preferences.getString(SCS_SETTING, ""))
        LOG.D(TAG, "Read preference Setting: $jSettings")

        // read setting from preferences
        try {
            mobileUid  = jSettings.getString(MOBILE_UID)
        } catch (e: Exception) {
            LOG.E(TAG, e.toString())
        }

        try {
            adAccount  = jSettings.getString(AD_ACCOUNT)
        } catch (e: Exception) {
            LOG.E(TAG, e.toString())
        }

        try {
            adPassword = jSettings.getString(AD_PASSWORD)
        } catch (e: Exception) {
            LOG.E(TAG, e.toString())
        }

        try {
            isRegister = jSettings.getBoolean(IS_REGISTER)
        } catch (e: Exception) {
            LOG.E(TAG, e.toString())
        }

        try {
            ImpApiService.SERVER_IP = jSettings.getString(NETWORK_DOMAIN)
        } catch (e: Exception) {
            LOG.E(TAG, e.toString())
        }

        try {
            scsRecordSerial = jSettings.getInt(RECORD_SERIAL)
            setRecordListFromJson(jSettings.getJSONObject(RECORD_DATA))
        } catch (e: Exception) {
            LOG.E(TAG, e.toString())
        }

        try {
            scsErrorLogSerial = jSettings.getInt(ERROR_SERIAL)
            setErrorListFromJson(jSettings.getJSONObject(ERROR_DATA))
        } catch (e: Exception) {
            LOG.E(TAG, e.toString())
        }
    }

    fun savePreferences(context: Context) {
        val preferences = context.getSharedPreferences("preference", Context.MODE_PRIVATE)
        val preferenceEditor = preferences.edit()

        preferenceEditor.putString(SCS_SETTING, packPreferencesToJson().toString())
        preferenceEditor.putString(APP_VERSION, getVersionName(context))
        preferenceEditor.apply()
    }

    /**
     * Get App version name
     */
    private fun getVersionName(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName
    }
}