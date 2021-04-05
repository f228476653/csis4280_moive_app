package com.example.movieapp.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilderRe {

    const val BASE_URL = "http://44.192.78.222:5000"
    const val URL = "$BASE_URL/"

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(BasicAuthInterceptor("anita", "anita123"))
        .build()
    //The Retrofit class generates an implementation of the Service interface.
    private val builder = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp)

    private val retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }
}