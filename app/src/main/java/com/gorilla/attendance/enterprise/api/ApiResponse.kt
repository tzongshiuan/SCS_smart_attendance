package com.gorilla.attendance.enterprise.api

import android.util.Log
import retrofit2.Response
import java.io.IOException

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class ApiResponse <T> {

    val code: Int

    private val body: T?

    private val errorMessage: String?

    val isSuccessful: Boolean
        get() = code in 200..299

    constructor(error: Throwable) {
        code = 500
        body = null
        errorMessage = error.message
    }

    constructor(response: Response<T>) {
        code = response.code()
        if (response.isSuccessful) {
            body = response.body()
            errorMessage = null
        } else {
            var message: String? = null
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody()!!.string()
                } catch (ignored: IOException) {
                    Log.e( "ApiResponse","error while parsing response",ignored)
                }

            }
            if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            errorMessage = message
            body = null
        }
    }
}