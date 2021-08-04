package com.local.foodshop.api

import com.local.foodshop.appConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiFactory {
    companion object{
        private const val BASE_URL = appConfig.SERVER_URL
    }

    private fun getRetrofit() :  Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://192.168.68.1:81/android/food_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    val serverApi : ServerApi = getRetrofit().create(ServerApi::class.java)
}