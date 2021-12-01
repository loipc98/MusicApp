package com.example.appmusic_mediastyle.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appmusic_mediastyle.MyMusicService
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_UPDATE_SEEKBAR
import com.example.appmusic_mediastyle.api.RetrofitClient.retrofitBuilder
import com.example.appmusic_mediastyle.model.MusicData
import com.example.musicappmock.model.Song
import com.example.musicappmock.model.Top100OfCountry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicViewModel : ViewModel() {
    var liveDataMusicData: MutableLiveData<MusicData> = MutableLiveData()
    var listTop100Music: MutableList<Top100OfCountry>? = null
    var currentListSongs: List<Song>
    var currentTotalTimeSong: Int = 0
    var currentPositionTimeSong: Int = 0
    var currentPositionSong: Int = 0
    var updateSeekBar:Int = 0
    var isPlaying = false
    var isDisplayLayoutPlayer = false
    var isLoadData = false
    var iDFragment = HOME_FRAGMENT_ID

    companion object{
        const val HOME_FRAGMENT_ID = 1
        const val DETAILS_FRAGMENT_ID = 2

    }

    init {
        listTop100Music = ArrayList()
        currentListSongs = ArrayList()
    }

    fun makeCallMusicDataAPI() {
        CoroutineScope(Dispatchers.IO).launch {
            retrofitBuilder.getMusicData().enqueue(object : Callback<MusicData> {
                override fun onResponse(call: Call<MusicData>, response: Response<MusicData>) {
                    liveDataMusicData.postValue(response.body())
                }

                override fun onFailure(call: Call<MusicData>, t: Throwable) {
                    liveDataMusicData.postValue(null)
                }

            })
        }
    }

    fun nextSong() {
        if (currentListSongs.isEmpty()) return
        if (currentPositionSong < currentListSongs.size - 1) currentPositionSong += 1
        else currentPositionSong = 0
    }

    fun backSong() {
        if (currentListSongs.isEmpty()) return
        if (currentPositionSong > 0) currentPositionSong -= 1
        else currentPositionSong = currentListSongs.size - 1
    }

    fun sendSongToService(context: Context) {
        if (currentListSongs.isEmpty()) return
        val intent = Intent(context, MyMusicService::class.java)
        val bundle = Bundle()
        bundle.putSerializable("object_song", currentListSongs[currentPositionSong])
        intent.putExtras(bundle)
        context.startService(intent)
    }

    fun sendActionToService(action: Int, context: Context) {
        val intent = Intent(context, MyMusicService::class.java)
        if(action == ACTION_UPDATE_SEEKBAR) intent.putExtra("update_seekbar",updateSeekBar)
        intent.putExtra("action_music_service", action)
        context.startService(intent)
    }

    fun getStringCurrentTimeSong(): String {
        val hours = currentPositionTimeSong / (1000 * 60 * 60)
        val minutes = currentPositionTimeSong % (1000 * 60 * 60) / (1000 * 60)
        val seconds = currentPositionTimeSong % (1000 * 60 * 60) % (1000 * 60) / 1000
        return if (hours == 0) "$minutes:${if (seconds < 10) "0$seconds" else "$seconds"}"
        else "$hours:${if (minutes < 10) "0$minutes" else "$minutes"}:${if (seconds < 10) "0$seconds" else "$seconds"}"
    }

    fun getStringTotalTimeSong(): String {
        val hours = currentTotalTimeSong / (1000 * 60 * 60)
        val minutes = currentTotalTimeSong % (1000 * 60 * 60) / (1000 * 60)
        val seconds = currentTotalTimeSong % (1000 * 60 * 60) % (1000 * 60) / 1000
        return if (hours == 0) "$minutes:${if (seconds < 10) "0$seconds" else "$seconds"}"
        else "$hours:${if (minutes < 10) "0$minutes" else "$minutes"}:${if (seconds < 10) "0$seconds" else "$seconds"}"
    }
}