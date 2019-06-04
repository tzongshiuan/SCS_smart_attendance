package com.gorilla.attendance.enterprise.ui.motp

import com.google.gson.Gson
import com.gorilla.attendance.enterprise.api.ImpApiService
import com.gorilla.attendance.enterprise.datamodel.scs.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observable
import io.reactivex.functions.Function
import retrofit2.Response

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/26
 * Description:
 */
class MotpRepository {

    fun saAuth(body: SaAuthPostBody): Observable<ScsAuthResponse> {
        return ImpApiService.getInstance()
                .saAuth(body)
                .subscribeOn(Schedulers.io())
                .map(object: Function<Response<ScsAuthResponse>, ScsAuthResponse> {
                    override fun apply(response: Response<ScsAuthResponse>): ScsAuthResponse? {
                        return if (response.isSuccessful) {
                            response.body()
                        } else {
                            val parser = Gson()
                            parser.fromJson(response.errorBody()?.string(), ScsAuthResponse::class.java)
                        }
                    }
                })
    }

    fun bapVerify(body: ScsBapVerifyPostBody): Observable<ScsAuthResponse> {
        return ImpApiService.getInstance()
                .bapVerify(body)
                .subscribeOn(Schedulers.io())
                .map(object: Function<Response<ScsAuthResponse>, ScsAuthResponse> {
                    override fun apply(response: Response<ScsAuthResponse>): ScsAuthResponse? {
                        return if (response.isSuccessful) {
                            response.body()
                        } else {
                            val parser = Gson()
                            parser.fromJson(response.errorBody()?.string(), ScsAuthResponse::class.java)
                        }
                    }
                })
    }

    fun scsRecords(body: ScsRecordPostBody): Observable<ScsAuthResponse> {
        return ImpApiService.getInstance()
                .scsRecords(body)
                .subscribeOn(Schedulers.io())
                .map(object: Function<Response<ScsAuthResponse>, ScsAuthResponse> {
                    override fun apply(response: Response<ScsAuthResponse>): ScsAuthResponse? {
                        return if (response.isSuccessful) {
                            response.body()
                        } else {
                            val parser = Gson()
                            parser.fromJson(response.errorBody()?.string(), ScsAuthResponse::class.java)
                        }
                    }
                })
    }

    fun scsUnrecognizedLog(body: ScsUnrecognizePostBody) : Observable<ScsAuthResponse> {
        return ImpApiService.getInstance()
                .scsUnrecognizedLog(body)
                .subscribeOn(Schedulers.io())
                .map(object: Function<Response<ScsAuthResponse>, ScsAuthResponse> {
                    override fun apply(response: Response<ScsAuthResponse>): ScsAuthResponse? {
                        return if (response.isSuccessful) {
                            response.body()
                        } else {
                            val parser = Gson()
                            parser.fromJson(response.errorBody()?.string(), ScsAuthResponse::class.java)
                        }
                    }
                })
    }
}