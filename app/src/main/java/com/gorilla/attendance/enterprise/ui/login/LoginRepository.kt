package com.gorilla.attendance.enterprise.ui.login

import com.google.gson.Gson
import com.gorilla.attendance.enterprise.api.ImpApiService
import com.gorilla.attendance.enterprise.datamodel.scs.AdAuthPostBody
import com.gorilla.attendance.enterprise.datamodel.scs.ScsAuthResponse
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class LoginRepository {

    fun login(username: String, password: String): Observable<ScsAuthResponse> {
        val body = AdAuthPostBody()
        body.username = username
        body.password = password

        return ImpApiService.getInstance()
                .adAuth(body)
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