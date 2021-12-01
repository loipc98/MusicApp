package com.example.appmusic_mediastyle.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.appmusic_mediastyle.R
import com.example.appmusic_mediastyle.viewmodel.MusicViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel:MusicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]

        supportFragmentManager.beginTransaction().add(R.id.frag_container, HomeFragment()).commit()

    }
}