package com.bagas.messagingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bagas.messagingapp.services.NotificationReceiver
import com.bagas.messagingapp.services.VoicePlayerService

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val text = intent.getStringExtra("data")
        text?.let {
            findViewById<TextView>(R.id.textView).text = it
        }

        findViewById<Button>(R.id.btn_stop_service)
            .setOnClickListener {
//                stopService(Intent(applicationContext, VoicePlayerService::class.java))

                sendBroadcast(Intent(NotificationReceiver.ACTION_STOP_VOICE))
            }
    }

}