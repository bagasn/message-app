package com.bagas.messagingapp

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bagas.messagingapp.livedata.UserLiveData
import com.bagas.messagingapp.services.VoiceReceiver
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        buttonFuckYou.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        init()
    }

    private fun init() {
        registerVoiceReceiver()

        getTokenFirebase()
    }

    private fun registerVoiceReceiver() {
        val receiver = VoiceReceiver()
        val filter = IntentFilter().apply {
            addAction(VoiceReceiver.ACTION_START_VOICE)
            addAction(VoiceReceiver.ACTION_STOP_VOICE)
        }

        registerReceiver(receiver, filter)
    }

    private fun snackMessage(msg: String, color: Int) {
        Snackbar.make(findViewById(R.id.coordinator), msg, Snackbar.LENGTH_INDEFINITE)
            .setTextColor(color)
            .setAction("Close") {}
            .setActionTextColor(Color.RED)
            .show()
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("demo")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    snackMessage("Success subscribe topic", Color.GREEN)
                } else {
                    snackMessage("Failed subscribe topic", Color.RED)
                }
            }
    }

    private fun getTokenFirebase() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                val sp = getSharedPreferences("app_session", MODE_PRIVATE)
                if (task.isSuccessful) {
                    sp.edit()
                        .putString("firebase_token", task.result)
                        .apply()
                }

                Log.i(
                    "Firebase", "Firebase Token: "
                            + sp.getString("firebase_token", "none")
                )
            }
    }
}