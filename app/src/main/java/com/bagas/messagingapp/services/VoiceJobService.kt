package com.bagas.messagingapp.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.media.MediaPlayer
import android.util.Log
import com.bagas.messagingapp.R
import com.bagas.messagingapp.util.SPManager

class VoiceJobService: JobService() {

    companion object {
        private const val TAG = "VoiceJobService"

        const val JOB_ID = 101
    }

    private lateinit var session: SPManager

    override fun onCreate() {
        super.onCreate()
        session = SPManager(applicationContext)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Voice job is started")
        playVoice(params)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.e(TAG, "Voice job has stopped")
        return true
    }

    private fun playVoice(params: JobParameters?) {
        if (session.orderCounter <= 0) {
            Log.d(TAG, "Invoke jobFinished")
            jobFinished(params, false)
            return
        }

        val media = MediaPlayer.create(applicationContext, R.raw.ringtone_message)
        media.setOnCompletionListener {
            it.release()

            playVoice(params)
        }
        media.start()
    }
}