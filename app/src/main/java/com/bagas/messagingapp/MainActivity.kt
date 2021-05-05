package com.bagas.messagingapp

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
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
import java.io.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        buttonFuckYou.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        buttonCopy.setOnClickListener {
            copyAudioFileToStorage()
        }

        init()
    }

    private fun init() {
        getTokenFirebase()
    }

    private fun copyAudioFileToStorage() {
        Log.i(TAG, "copyAudioFileToStorage")
        val assetStream = assets.open("ringtone_message.mp3")
        val fileTarget = File(
            getExternalFilesDir(Environment.DIRECTORY_RINGTONES),
            "notify_voice.mp3"
        )

        try {
            val outputStream = FileOutputStream(fileTarget)
            startCopingFile(assetStream, outputStream)

            assetStream.close()

            outputStream.flush()
            outputStream.close()
            Log.i(TAG, "Ringtone file has been copied")
        } catch (e: IOException) {
            Log.e(TAG, "Error while coping file", e)
        }

    }

    @Throws(IOException::class)
    private fun startCopingFile(input: InputStream, output: OutputStream) {
        var buffer = ByteArray(1024)
        var read: Int

        do {
            read = input.read(buffer)

            if (read != -1) {
                output.write(buffer, 0, read)
            }
        } while (read != -1)

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
