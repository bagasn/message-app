package com.bagas.messagingapp.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.bagas.messagingapp.util.SPManager
import com.bagas.messagingapp.util.ScheduleManager
import java.io.File

class VoiceJobService : JobService(), MediaPlayer.OnPreparedListener {

    companion object {
        private const val TAG = "VoiceJobService"

        const val JOB_ID = 101
    }

    private lateinit var session: SPManager
    private lateinit var mScheduler: ScheduleManager
    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        session = SPManager(applicationContext)
        mScheduler = ScheduleManager.with(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
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

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.apply {
            isLooping = true
            start()
        }
    }

    private fun playVoice(params: JobParameters?) {
        initMedia()
        MyThread(params).start()
    }

    private fun initMedia() {
        val fileAudio =
            File(getExternalFilesDir(Environment.DIRECTORY_RINGTONES), "notify_voice.mp3")
        val audioUri = Uri.parse(fileAudio.absolutePath)

        if (mMediaPlayer != null) {
            mMediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
                mMediaPlayer = null
            }
        }

        mMediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, audioUri)
            setOnPreparedListener(this@VoiceJobService)
            try {
                prepareAsync()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "onPrepare error", e)
            }
        }
    }

    inner class MyThread(
        private val mJobParameters: JobParameters?
    ) : Thread() {

        override fun run() {
            super.run()
            Log.i(TAG, "Thread is running with id: ${currentThread().id}")

            while (session.orderCounter > 0) {
                try {
                    sleep(500)

                    Log.d(TAG, "Is media playing? ${mMediaPlayer?.isPlaying}")
                    if (session.orderCounter > 0) {
                        mMediaPlayer?.let {
                            if (!it.isPlaying) {
                                playVoice(mJobParameters)
                            }
                        }
                    } else {
                        mMediaPlayer?.apply {
                            if (isPlaying) {
                                stop()
                            }

                            reset()
                            release()
                            jobFinished(mJobParameters, false)
                            Log.w(TAG, "Invoke jobFinished from thread")
                        }
                    }
                } catch (e: InterruptedException) {
                    Log.e(TAG, "InterruptedException while Time Loop", e)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception while Time Loop", e)

                    playVoice(mJobParameters)
                    Log.i(TAG, "Re-play audio from thread")
                }
            }
        }
    }
}