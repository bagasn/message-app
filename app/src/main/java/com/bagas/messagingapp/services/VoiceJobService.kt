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
    private var mThread: MyThread? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        session = SPManager(applicationContext)
        mScheduler = ScheduleManager.with(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")

        mThread?.let {

        }

        mMediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }

            it.reset()
            it.release()
        }
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Voice job is started")

        if (session.orderCounter > 0) {
            playVoice(params)
        } else {
            jobFinished(params, false)
            Log.w(TAG, "Invoke jobFinished immediately")
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.w(TAG, "Voice job has stopped")
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

        if ((mThread == null) or (mThread?.isAlive != true)) {
            mThread = MyThread(params).apply {
                start()
            }
        }
    }

    private fun initMedia() {
        mMediaPlayer?.let {
            if (it.isPlaying) {
                return
            }
        }

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
                    sleep(1000)

                    if (session.orderCounter > 0) {
                        if (mMediaPlayer == null) {
                            playVoice(mJobParameters)
                            break
                        } else {
                            if (mMediaPlayer?.isPlaying == false) {
                                playVoice(mJobParameters)
                                break
                            }
                        }
                    }
                } catch (e: InterruptedException) {
                    Log.e(TAG, "InterruptedException while Time Loop", e)
                } catch (e: IllegalStateException) {
                    Log.e(TAG, "Exception while Time Loop", e)
                } finally {
                    Log.d(TAG, "Thread is looping")
                    Log.d(TAG, "MediaPlayer is null? $mMediaPlayer")
                }
            }

            if (session.orderCounter <= 0) {
                jobFinished(mJobParameters, false)
                Log.w(TAG, "Invoke jobfinished from Thread")
            }
            Log.w(TAG, "Thread has finished")
        }
    }
}