package com.example.appmusic_mediastyle

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyMusicReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent!=null){
            val action:Int = intent.getIntExtra("action_music",0)
            val mServiceIntent = Intent(context,MyMusicService::class.java)
            mServiceIntent.putExtra("action_music_service", action)
            context?.startService(mServiceIntent)
        }
    }
}