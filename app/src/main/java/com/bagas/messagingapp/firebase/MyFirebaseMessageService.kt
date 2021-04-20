package com.bagas.messagingapp.firebase

import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.bagas.messagingapp.SecondActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessageService : FirebaseMessagingService() {

    companion object {
        private val TAG = "Firebase Service"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.i(TAG, "payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.i(TAG, "notification: ${remoteMessage.notification}")

//            val intent = Intent(this, SecondActivity::class.java)


            val notify = NotificationUtil.createNotification(
                applicationContext,
                it.title,
                it.body,
                null
            )

            val manager = NotificationManagerCompat.from(this)
            manager.notify(1, notify)
        }
    }

    override fun onNewToken(token: String) {
        val sp = getSharedPreferences("app_session", MODE_PRIVATE)

        sp.edit().putString("firebase_token", token)
            .apply()
    }
}