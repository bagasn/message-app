package com.bagas.messagingapp.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.bagas.messagingapp.R

class VoicePlayerService: Service() {

    private val TAG = "VoicePlayer"
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: ")

        mediaPlayer = createMediaPlayer()
        mediaPlayer?.start()
    }

    private fun createMediaPlayer(): MediaPlayer {
        return MediaPlayer.create(this, R.raw.ringtone_message).apply {
            setOnCompletionListener {
                it.release()

                mediaPlayer = createMediaPlayer()
                mediaPlayer?.start()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: ${intent?.data}")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")

        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

}