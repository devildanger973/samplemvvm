package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("TAG", "onMessageReceived")

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            createNotificationChannel(it.title.toString())
            Log.d("TAG", "remoteMessage ${it.body} ${it.title}")
            setNotificationChannelIntent(it.title.toString())
        }

    }

    override fun onNewToken(token: String) {
        Log.d("TAG", "Refreshed token: $token")
        super.onNewToken(token)
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
    }

    private fun setNotificationChannelIntent(id: String) {
        val builder = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(id)
            .setContentText(id)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(id)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel(id: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val name = getString(R.string.msg_token_fmt)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            val builder = NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(id)
                .setContentText(id)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(id)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true)
            notificationManager.notify(2, builder.build())
        }
    }
}