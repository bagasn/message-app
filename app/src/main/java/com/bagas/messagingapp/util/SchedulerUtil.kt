package com.bagas.messagingapp.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.bagas.messagingapp.services.VoiceJobService

class SchedulerUtil private constructor(private val context: Context) {

    companion object {
        fun with(context: Context): SchedulerUtil {
            return SchedulerUtil(context)
        }
    }

    fun startJob(jobId: Int, clazz: Class<*>): Int {
        val job = getJobInfo(jobId, getComponentName(clazz))

        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        return scheduler.schedule(job)
    }

    fun stopJob(jobId: Int) {
        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(jobId)
    }

    private fun getComponentName(clazz: Class<*>) = ComponentName(context, clazz)

    private fun getJobInfo(jobId: Int, component: ComponentName): JobInfo {
        return JobInfo.Builder(jobId, component)
            .build()
    }

}