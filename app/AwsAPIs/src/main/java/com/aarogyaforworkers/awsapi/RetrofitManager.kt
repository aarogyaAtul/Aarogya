package com.aarogyaforworkers.awsapi

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {

    private val baseURL = "https://niqmb839bd.execute-api.ap-south-1.amazonaws.com/test/"

    val client = OkHttpClient()
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T: Any> myApi(classType: Class<T>): T {
        return retrofit.create(classType)
    }


}

