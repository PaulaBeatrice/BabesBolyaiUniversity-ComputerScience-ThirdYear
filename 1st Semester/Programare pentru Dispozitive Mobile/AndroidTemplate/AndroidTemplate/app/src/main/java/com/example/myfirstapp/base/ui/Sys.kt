package com.example.myfirstapp.base.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myfirstapp.R

class Sys {
    companion object {
        private var mToast: Toast? = null

        fun Context.makeToast(message: String) {
            Handler(Looper.getMainLooper()).post({
                mToast?.cancel()
                mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
                mToast?.show()
            })
        }

        @JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence = "Verbose WorkManager Notifications"
        private const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
        @JvmField val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
        private const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
        private const val NOTIFICATION_ID = 1

        fun Context.makeStatusNotification(message: String) {
            // Make a channel if necessary
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
            val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            // Add the channel
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)

            // Create the notification
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(LongArray(0))

            // Show the notification
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                return
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build())
        }
    }
}