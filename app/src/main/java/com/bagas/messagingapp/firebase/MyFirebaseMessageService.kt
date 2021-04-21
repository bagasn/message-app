package com.bagas.messagingapp.firebase

import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.bagas.messagingapp.R
import com.bagas.messagingapp.SecondActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessageService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "Firebase Service"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.i(TAG, "payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.i(TAG, "notification: title = ${it.title}")
            Log.i(TAG, "notification: body = ${it.body}")
            Log.i(TAG, "notification: clickAction = ${it.clickAction}")


            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("orderId", it.clickAction)

            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notify = NotificationUtil.createNotification(
                applicationContext,
                it.title,
                it.body,
                pendingIntent
            )

            val manager = NotificationManagerCompat.from(this)
            manager.notify(1, notify)
            playMedia()
        }
    }

    override fun onNewToken(token: String) {
        val sp = getSharedPreferences("app_session", MODE_PRIVATE)

        sp.edit().putString("firebase_token", token)
            .apply()
    }

    private fun playMedia() {
        val media = MediaPlayer.create(this, R.raw.ringtone_message)
        media.isLooping = true
        media.start()
    }
}