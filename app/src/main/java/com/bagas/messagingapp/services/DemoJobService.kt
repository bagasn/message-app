package com.bagas.messagingapp.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class DemoJobService : JobService() {
    private val TAG = "DemoJobService"

    private var jobCounter = 0

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob")

        jobCounter++
        Log.d(TAG, "Job has start $jobCounter time.")
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "onDestroy")
    }

    companion object {
        public const val JOB_ID = 10001
    }

}