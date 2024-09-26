package com.funyaani.todolist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat




class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("title")
        val description = intent?.getStringExtra("description")

        if (title != null && description != null) {
            val notification = NotificationCompat.Builder(context, "todo_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // Change to your app's icon
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}





