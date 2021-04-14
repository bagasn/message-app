package com.bagas.messagingapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationUtil {

    public static Notification createNotification(Context context, String title, String body) {
        NotificationCompat.Builder notification = createNotifyBuilder(context);
        notification.setContentTitle(title);
        notification.setContentText(body);
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setPriority(Notification.PRIORITY_DEFAULT);

        return notification.build();
    }


    private static NotificationCompat.Builder createNotifyBuilder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new NotificationCompat.Builder(context, createNotificationChannel(context));
        } else {
            return new NotificationCompat.Builder(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String createNotificationChannel(Context context) {
        String channelId = "channel_user_order";
        String name = "User Order";
        String description = "Notification User Order";

        NotificationChannel channel = new NotificationChannel(
                channelId, name, NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

}
