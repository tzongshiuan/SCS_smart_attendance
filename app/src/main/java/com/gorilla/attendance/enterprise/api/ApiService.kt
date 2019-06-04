package com.gorilla.attendance.enterprise.api

import com.gorilla.attendance.enterprise.datamodel.scs.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
interface ApiService {

    @POST("scs/user/adAuth")
    fun adAuth(@Body body: AdAuthPostBody) : Observable<Response<ScsAuthResponse>>

    /**
     * mobileUid, username, pinCode, emergencyCode, createTime, imageList
     */
    @POST("scs/user/register")
    fun register(@Body body: RegisterPostBody) : Observable<Response<ScsAuthResponse>>

    /**
     * mobileUid, username, pinCode
     */
    @POST("scs/user/saAuth")
    fun saAuth(@Body body: SaAuthPostBody) : Observable<Response<ScsAuthResponse>>

    /**
     * id, type, imageList
     */
    @POST("api/V1_1beta/BAP/verify")
    fun bapVerify(@Body body: ScsBapVerifyPostBody) : Observable<Response<ScsAuthResponse>>

    /**
     * serial, username, pinCode, deviceTime, faceVerify, faceImg, mobileUid, loginMode, gps
     */
    @POST("scs/access/records")
    fun scsRecords(@Body body: ScsRecordPostBody) : Observable<Response<ScsAuthResponse>>

    /**
     * serial, deviceTime, faceImg, username, mobileUid, pinCode, gps
     */
    @POST("scs/access/unrecognizedLog")
    fun scsUnrecognizedLog(@Body body: ScsUnrecognizePostBody) : Observable<Response<ScsAuthResponse>>
}