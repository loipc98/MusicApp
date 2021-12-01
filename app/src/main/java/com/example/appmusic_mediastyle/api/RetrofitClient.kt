package com.example.appmusic_mediastyle.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {
    private const val BASE_URL = "https://api.apify.com/v2/key-value-stores/EJ3Ppyr2t73Ifit64/records/"

    val retrofitBuilder: ApiService = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
        .create(ApiService::class.java)

}