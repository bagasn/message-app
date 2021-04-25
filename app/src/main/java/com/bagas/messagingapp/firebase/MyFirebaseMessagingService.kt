package com.bagas.messagingapp.firebase

import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.bagas.messagingapp.R
import com.bagas.messagingapp.SecondActivity
import com.bagas.messagingapp.services.NotificationReceiver
import com.bagas.messagingapp.services.VoicePlayerService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "Firebase Service"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data

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
        manager.notify(1, notify)
        playMedia()
    }

    override fun onNewToken(token: String) {
        val sp = getSharedPreferences("app_session", MODE_PRIVATE)

        sp.edit().putString("firebase_token", token)
            .apply()
    }

    private fun playMedia() {
//        val intent = Intent(this, VoicePlayerService::class.java)
//        startService(intent)

        val broadcast = Intent()
        broadcast.action = NotificationReceiver.ACTION_START_VOICE
        sendBroadcast(broadcast)
    }

    private fun mapToJsonString(data: Map<String, String>): String {
        return JSONObject(data).toString()
    }
}