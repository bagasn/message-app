package com.bagas.messagingapp.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.util.Log

class ScheduleManager private constructor(private val context: Context) {

    private val TAG = "SchedulerManager"

    companion object {
        fun with(context: Context): ScheduleManager {
            return ScheduleManager(context)
        }
    }

    private val mJobScheduler: JobScheduler =
        context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

    fun startJob(jobId: Int, clazz: Class<*>): Int {
        val job = getJobInfo(jobId, getComponentName(clazz))

        return mJobScheduler.schedule(job)
    }

    fun stopJob(jobId: Int) {
        mJobScheduler.cancel(jobId)
        Log.d(TAG, "invoke cancel scheduler")
    }

    fun getJob(jobId: Int): JobInfo? {
        for (jobInfo in mJobScheduler.allPendingJobs) {
            if (jobInfo.id == jobId) {
                Log.i(TAG, "Get job with id $jobId")
                return jobInfo
            }
        }
        return null
    }

    fun isJobServiceRunning(jobId: Int): Boolean {
        var isServiceRunning = false

        for (info in mJobScheduler.allPendingJobs) {
            if (info.id == jobId) {
                isServiceRunning = true
                break
            }
        }

        Log.i(TAG, "Job with id $jobId is running? $isServiceRunning")
        return isServiceRunning
    }

    private fun getComponentName(clazz: Class<*>) = ComponentName(context, clazz)

    private fun getJobInfo(jobId: Int, component: ComponentName): JobInfo {
        return JobInfo.Builder(jobId, component)
            .build()
    }

}