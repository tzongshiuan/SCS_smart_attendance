package com.gorilla.attendance.enterprise.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/25
 * Description:
 */
class ImpApiService {
    companion object {
        private var instance: ApiService? = null

        var SERVER_IP = "http://192.168.11.178/SeDemo/"

        fun getInstance(): ApiService {
            if (instance == null) {
//                val ipSelectInterceptor = object : HostSelectionInterceptor(preferencesHelper.ip){
//                    override var host:String = preferencesHelper.ip
//                        get() = preferencesHelper.ip
//                }

                val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                }

                val client : OkHttpClient = OkHttpClient.Builder().apply {
//                    this.addInterceptor(ipSelectInterceptor)
                    this.addInterceptor(interceptor)
                }.readTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .build()

                try {
                    instance =  Retrofit.Builder()
                            .baseUrl(SERVER_IP)
//                            .baseUrl(preferencesHelper.ip)
//                            .baseUrl("http://192.168.10.224:13667/")
//                            .baseUrl("http://192.168.10.204:8081/")
//                            .baseUrl("http://192.168.10.228:8050/")
                            .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(client)   //show log
                            .build()
                            .create(ApiService::class.java)
                } catch (e: Exception){
                    instance = Retrofit.Builder()
                            .baseUrl(SERVER_IP)// and error server ip
//                    .baseUrl(preferencesHelper.getServerIp())
//                .baseUrl("http://192.168.10.204:8081/")
//                .baseUrl("http://192.168.10.228:8050/")
                            .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(client)   //show log
                            .build()
                            .create(ApiService::class.java)
                }
            }

            return instance!!
        }
    }
}