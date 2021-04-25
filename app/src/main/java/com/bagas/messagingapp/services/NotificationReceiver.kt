package com.bagas.messagingapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.bagas.messagingapp.R

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_START_VOICE = "com.bagas.messagingapp.START_VOICE_ORDER"
        const val ACTION_STOP_VOICE = "com.bagas.messagingapp.STOP_VOICE_ORDER"
    }

    private val TAG = "NotificationReceiver"
    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive")
        intent?.let {
            when (it.action) {
                ACTION_START_VOICE -> {
                    mediaPlayer = MediaPlayer.create(context, R.raw.ringtone_message)
                    mediaPlayer?.isLooping = true
                    mediaPlayer?.start()
                }
                else -> {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                }
            }
        }
    }

    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        return super.peekService(myContext, service)
    }
}