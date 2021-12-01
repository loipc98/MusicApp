package com.example.appmusic_mediastyle

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.appmusic_mediastyle.MyMusicApplication.Companion.CHANNEL_ID
import com.example.musicappmock.model.Song
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.lang.Runnable
import java.net.HttpURLConnection
import java.net.URL

class MyMusicService : Service() {

    private var mSong: Song? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val bundle: Bundle? = intent?.extras
        val song = bundle?.get("object_song")
        if (song != null) {
            mSong = song as Song
            handleActionMusic(ACTION_START)
        }

        if (intent != null) {
            val actionMusic = intent.getIntExtra("action_music_service", 0)
            if(actionMusic== ACTION_UPDATE_SEEKBAR && mediaPlayer!=null) {
                val currentPosition = intent.getIntExtra("update_seekbar",0)
                mediaPlayer!!.seekTo(currentPosition)
            }
            handleActionMusic(actionMusic)
        }
        updateTimeSong()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    private fun getBitMapFromURL(src: String): Bitmap {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input:InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            BitmapFactory.decodeResource(resources, R.drawable.loaderror)
        }
    }

    private suspend fun sendNotification(mSong: Song?) {
        if (mSong == null) return

        val bitmap =  CoroutineScope(Dispatchers.IO).async{
            Glide.with(applicationContext)
                .asBitmap()
                .load(mSong.avatar)
                .submit()
                .get()
        }

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_music)
            .setSubText("Dang Loi")
            .setContentTitle(mSong.title)
            .setContentText(mSong.creator)
            .setLargeIcon(bitmap.await())
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )



        if (isPlaying) {
            //Add action
            notificationBuilder.addAction(
                R.drawable.ic_previous,
                "Previous",
                getPendingIntent(this, ACTION_PREVIOUS)
            )
                .addAction(R.drawable.ic_pause, "Pause", getPendingIntent(this, ACTION_PAUSE))
                .addAction(R.drawable.ic_next, "Next", getPendingIntent(this, ACTION_NEXT))
                .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(this, ACTION_CLEAR))
        } else {
            //Add action
            notificationBuilder.addAction(
                R.drawable.ic_previous,
                "Previous",
                getPendingIntent(this, ACTION_PREVIOUS)
            )
                .addAction(R.drawable.ic_play, "Play", getPendingIntent(this, ACTION_RESUME))
                .addAction(R.drawable.ic_next, "Next", getPendingIntent(this, ACTION_NEXT))
                .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(this, ACTION_CLEAR))
        }

        startForeground(1, notificationBuilder.build())
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_START -> {
                startSong(mSong)
            }
            ACTION_RESUME -> {
                resumeSong(mSong)
            }
            ACTION_PAUSE -> {
                pauseSong()
            }
            ACTION_CLEAR -> {
                stopSelf()
                sendActionToActivity(ACTION_CLEAR)
            }
            ACTION_NEXT -> {
                sendActionToActivity(ACTION_NEXT)
            }
            ACTION_PREVIOUS -> {
                sendActionToActivity(ACTION_PREVIOUS)
            }
            ACTION_UPDATE_TIME -> {
                sendActionToActivity(ACTION_UPDATE_TIME)
            }
        }
    }

    private fun resumeSong(mSong: Song?) {
        if (mSong == null || mediaPlayer == null) return
        else {
            mediaPlayer?.start()
            isPlaying = true
            CoroutineScope(Dispatchers.IO).launch {
                sendNotification(mSong)
            }
            sendActionToActivity(ACTION_RESUME)
        }
    }

    private fun startSong(mSong: Song?) {
        if (mSong == null) return
        mediaPlayer?.stop()
        mediaPlayer?.release()
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(mSong.music)
            mediaPlayer!!.prepareAsync()
        } catch (e: Exception) {
        }
        mediaPlayer!!.setOnPreparedListener {
            mediaPlayer!!.start()
            isPlaying = true
            CoroutineScope(Dispatchers.IO).launch {
                sendNotification(mSong)
            }
            sendActionToActivity(ACTION_START)
        }
    }

    private fun pauseSong() {
        if (mSong == null || mediaPlayer == null) return
        isPlaying = false
        mediaPlayer!!.pause()
        CoroutineScope(Dispatchers.IO).launch {
            sendNotification(mSong)
        }
        sendActionToActivity(ACTION_PAUSE)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getPendingIntent(context: Context, action: Int): PendingIntent {
        val intent = Intent(this, MyMusicReceiver::class.java)
        intent.putExtra("action_music", action)
        return PendingIntent.getBroadcast(
            context.applicationContext,
            action,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun sendActionToActivity(action: Int) {
        val intent = Intent("send_music_data_to_activity")
        val bundle = Bundle()
        bundle.putBoolean("status_player", isPlaying)
        bundle.putInt("action_music", action)
        if (action == ACTION_START) bundle.putInt("total_time_song", getTotalTimeSong())
        if (action == ACTION_UPDATE_TIME) bundle.putInt("current_position_time_song", getCurrentTimeSong())
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun getTotalTimeSong(): Int {
        if (mediaPlayer == null) return 0
        return mediaPlayer!!.duration
    }

    private fun getCurrentTimeSong(): Int {
        if (mediaPlayer == null) return 0
        return mediaPlayer!!.currentPosition
    }

    private fun updateTimeSong() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    sendActionToActivity(ACTION_UPDATE_TIME)
                    mediaPlayer?.setOnCompletionListener {
                        sendActionToActivity(ACTION_END_SONG)
                    }
                    handler.postDelayed(this, 500)
                } catch (e: Exception) {

                }
            }
        }, 100)
    }


    companion object {
        const val ACTION_START = 1
        const val ACTION_PAUSE = 2
        const val ACTION_RESUME = 3
        const val ACTION_CLEAR = 4
        const val ACTION_NEXT = 5
        const val ACTION_PREVIOUS = 6
        const val ACTION_UPDATE_TIME = 7
        const val ACTION_UPDATE_SEEKBAR = 9
        const val ACTION_END_SONG = 9
    }
}