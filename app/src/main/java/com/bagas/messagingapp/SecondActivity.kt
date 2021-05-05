package com.bagas.messagingapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bagas.messagingapp.util.SPManager

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

//                sendBroadcast(Intent(VoiceReceiver.ACTION_STOP_VOICE))

                SPManager.with(applicationContext)
                    .orderCounter = 0
                SPManager.with(applicationContext)
                    .isVoicePlay = false
            }
    }

}