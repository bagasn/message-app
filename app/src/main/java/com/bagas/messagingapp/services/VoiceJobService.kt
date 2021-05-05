package com.bagas.messagingapp.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.bagas.messagingapp.R
import com.bagas.messagingapp.util.SPManager
import java.io.File

class VoiceJobService : JobService() {

    companion object {
        private const val TAG = "VoiceJobService"

        const val JOB_ID = 101
    }

    private lateinit var session: SPManager
    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        session = SPManager(applicationContext)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Voice job is started")
        if (!session.isVoicePlay) {
            playVoice(params)
        } else {
            jobFinished(params, false)
            Log.w(TAG, "Invoke job finish immediately")
        }
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
            session.isVoicePlay = false
            return
        }

        if (mMediaPlayer != null) {
            mMediaPlayer?.apply {
                reset()

                start()
            }
        } else {

            val assetFile = assets.openFd("ringtone_message.mp3")

            val fileAudio = File(getExternalFilesDir(Environment.DIRECTORY_RINGTONES), "notify_voice.mp3")
            val audioUri = Uri.parse(fileAudio.absolutePath)

            mMediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, audioUri)
                try {
                    prepare()
                } catch (e: IllegalStateException) {
                    Log.e(TAG, "onPrepare", e)
                }
                start()

                setOnCompletionListener { mp ->
                    Log.d(TAG, "MediaPlayer is Complete")
                    mp.release()

                    mMediaPlayer = null
                    playVoice(params)
                }

                setOnErrorListener { mp, what, extra ->
                    Log.e(TAG, "MediaPlayer has error. what $what, extra $extra")
                    false
                }

                setOnBufferingUpdateListener { mp, percent ->

                }
            }
        }

        session.isVoicePlay = true
    }
}