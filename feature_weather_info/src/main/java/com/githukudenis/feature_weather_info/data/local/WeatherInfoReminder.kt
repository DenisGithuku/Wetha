package com.githukudenis.feature_weather_info.data.local

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val notificationChannelId: String = "weather_reminders"

class WeatherInfoReminder : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.extras?.getString("message")?.let { message ->
            sendNotification(context, message)
        }
    }

    private fun sendNotification(context: Context?, message: String) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setContentTitle("Weather updates")
            .setContentText(message)
            .setPriority(Notification.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager
            .apply {
                notify(Math.random().toInt(), notification)
            }

    }
}