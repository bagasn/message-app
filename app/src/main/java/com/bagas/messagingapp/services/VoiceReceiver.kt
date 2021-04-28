package com.bagas.messagingapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import com.bagas.messagingapp.R

class VoiceReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "VoiceReceiver"
        const val ACTION_START_VOICE = "com.bagas.messagingapp.START_VOICE_ORDER"
        const val ACTION_STOP_VOICE = "com.bagas.messagingapp.STOP_VOICE_ORDER"
    }

    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive")

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.ringtone_message).apply {
                isLooping = true
            }
        }

        intent?.let { it ->
            when (it.action) {
                ACTION_START_VOICE -> {
                    mediaPlayer?.apply {
                        if (!isPlaying) {
                            start()
                        }
                    }
                }
                ACTION_STOP_VOICE -> {
                    mediaPlayer?.apply {
                        if (isPlaying) {
                            stop()
                            release()

                            mediaPlayer = null
                        }
                    }
                }
                else -> Log.e(TAG, "onReceive: Not supported action")
            }
        }
    }
}