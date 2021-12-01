package com.example.appmusic_mediastyle.model

import com.example.appmusic_mediastyle.model.Top100

data class Songs(
    val top100_AM: List<Top100>,
    val top100_CA: List<Top100>,
    val top100_KL: List<Top100>,
    val top100_VN: List<Top100>
)