package com.example.appmusic_mediastyle.model

import com.example.musicappmock.model.Song

data class Top100(
    val name: String,
    val songs: List<Song>,
    val url: String
)