package com.bagas.messagingapp.firebase

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.bagas.messagingapp.SecondActivity
import com.bagas.messagingapp.services.VoiceJobService
import com.bagas.messagingapp.util.SPManager
import com.bagas.messagingapp.util.ScheduleManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "Firebase Service"
    }

    private lateinit var spManager: SPManager
    private lateinit var mScheduler: ScheduleManager

    private var notifIdCounter = 1;

    override fun onCreate() {
        super.onCreate()

        spManager = SPManager.with(applicationContext)
        mScheduler = ScheduleManager.with(applicationContext)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data

            val orderStatus = data["orderStatus"]
            if (!orderStatus.isNullOrEmpty()) {
                spManager.orderCounter += 1
                Log.d(TAG, "onMessageReceived: order count after add ${spManager.orderCounter}")
            }

            showNotification(data["notify-title"], data["notify-body"], data)
        }

        remoteMessage.notification?.let {
            Log.i(TAG, "notification: title = ${it.title}")
            Log.i(TAG, "notification: body = ${it.body}")
            Log.i(TAG, "notification: clickAction = ${it.clickAction}")
        }
    }

    private fun showNotification(title: String?, body: String?, data: Map<String, String>) {
        val intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("data", mapToJsonString(data))

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notify = NotificationUtil.createNotification(
            applicationContext,
            title,
            body,
            pendingIntent
        )

        val manager = NotificationManagerCompat.from(this)
        manager.notify(notifIdCounter++, notify)
        playMedia()
    }

    override fun onNewToken(token: String) {
        spManager.firebaseToken = token
    }

    private fun playMedia() {
//        val intent = Intent(this, VoicePlayerService::class.java)
//        startService(intent)

//        val broadcast = Intent()
//        broadcast.action = VoiceReceiver.ACTION_START_VOICE
//        sendBroadcast(broadcast)

        if (!mScheduler.isJobServiceRunning(VoiceJobService.JOB_ID)) {
            mScheduler.startJob(VoiceJobService.JOB_ID, VoiceJobService::class.java)
        }
    }

    private fun mapToJsonString(data: Map<String, String>): String {
        return JSONObject(data).toString()
    }
}