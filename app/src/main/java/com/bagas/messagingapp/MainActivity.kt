package com.bagas.messagingapp

import android.app.job.JobScheduler
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.bagas.messagingapp.services.DemoJobService
import com.bagas.messagingapp.services.VoiceJobService
import com.bagas.messagingapp.services.VoiceReceiver
import com.bagas.messagingapp.util.SPManager
import com.bagas.messagingapp.util.ScheduleManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var spManager: SPManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        spManager = SPManager.with(applicationContext)

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

        checkJobSchedulerRunningService()
    }

    private fun checkJobSchedulerRunningService() {
        val scheduler = ScheduleManager.with(applicationContext)

        if (spManager.orderCounter > 0) {
            if (!scheduler.isJobServiceRunning(DemoJobService.JOB_ID)) {
                scheduler.startJob(VoiceJobService.JOB_ID, VoiceJobService::class.java)
            } else {
                scheduler.getJob(VoiceJobService.JOB_ID)
            }
        }
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
