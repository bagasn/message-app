package com.bagas.messagingapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bagas.messagingapp.services.DemoJobService
import com.bagas.messagingapp.services.VoiceJobService
import com.bagas.messagingapp.util.SPManager
import com.bagas.messagingapp.util.ScheduleManager
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {

    private lateinit var mScheduler: ScheduleManager
    private lateinit var session: SPManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        mScheduler = ScheduleManager.with(applicationContext)
        session = SPManager.with(applicationContext)

        val text = intent.getStringExtra("data")
        text?.let {
            findViewById<TextView>(R.id.textView).text = it
        }
    }

    private fun stopService() {
//        stopService(Intent(applicationContext, VoicePlayerService::class.java))
//
//        sendBroadcast(Intent(VoiceReceiver.ACTION_STOP_VOICE))

        SPManager.with(applicationContext)
            .orderCounter = 0
    }

    fun buttonClicked(view: View?) {
        when (view?.id) {
            R.id.btn_start_job -> {
//                if (!mScheduler.isJobServiceRunning(DemoJobService.JOB_ID)) {
                    mScheduler.startJob(VoiceJobService.JOB_ID, VoiceJobService::class.java)
                    Log.d(TAG, "starting job service")
//                }
            }
            R.id.btn_stop_job -> {
                Log.d(TAG, "invoke stopJob")
                mScheduler.stopJob(VoiceJobService.JOB_ID)
            }
            R.id.btn_check_job -> {
                mScheduler.isJobServiceRunning(VoiceJobService.JOB_ID)
            }
            R.id.btn_stop_service -> {
                stopService()
            }
        }
    }

    companion object {
        private const val TAG = "SecondActivity"
    }

}