package com.bagas.messagingapp.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.bagas.messagingapp.R
import com.bagas.messagingapp.livedata.UserLiveData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessageService : FirebaseMessagingService() {

    companion object {
        private val TAG = "Firebase Service"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Sender: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            for (item in remoteMessage.data) {
                Log.d(TAG, "field ${item.key} fill with: ${item.value}")
            }
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification title: ${it.title}")
            Log.d(TAG, "Message Notification Body: ${it.body}")
            val notify = createNotification(it.title!!, it.body!!)

            val manager = NotificationManagerCompat.from(this)
            manager.notify(1, notify)
        }
    }

    override fun onNewToken(token: String) {
        val sp = getSharedPreferences("app_session", MODE_PRIVATE)

        sp.edit().putString("firebase_token", token)
            .apply()
    }

    fun createNotification(title: String, body: String): Notification {
        val notify = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this, createNotificationChannel())
        } else {
            NotificationCompat.Builder(this)
        }

        notify.setSmallIcon(R.mipmap.ic_launcher)
        notify.setContentTitle(title)
        notify.setContentText(body)

        return notify.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "channel_order_id"
        val name = "Channel Order"

        val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "This channel use for order notification"

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        return channelId
    }
}