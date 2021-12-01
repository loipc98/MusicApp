package com.example.appmusic_mediastyle.repository

import com.example.appmusic_mediastyle.api.ApiService

class DataMusicRepository(private val apiService: ApiService) {

    suspend fun getAllDataMusic() = apiService.getMusicData()

}