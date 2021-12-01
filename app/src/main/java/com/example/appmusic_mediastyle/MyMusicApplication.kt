package com.example.appmusic_mediastyle

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyMusicApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        createChannelNotification()
    }

    private fun createChannelNotification() {
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID,"Channel Service Music",NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null,null)

            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            channel.enableLights(true)


            val manager: NotificationManager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

        }
    }

    companion object{
        const val CHANNEL_ID = "channel_service_music"
    }
}