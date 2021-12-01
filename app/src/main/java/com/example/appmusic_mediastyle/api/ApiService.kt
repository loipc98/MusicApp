package com.example.appmusic_mediastyle.api

import com.example.appmusic_mediastyle.model.MusicData
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("LATEST")
    fun getMusicData(): Call<MusicData>
}