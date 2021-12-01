package com.example.musicappmock.model

import java.io.Serializable

data class Song(
    val avatar: String,
    val bgImage: String,
    val coverImage: String,
    val creator: String,
    val lyric: String,
    val music: String,
    val title: String,
    val url: String
) : Serializable