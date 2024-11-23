package com.example.triviago

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.triviago.activities.HomeActivity

class TriviaNotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "trivia_notifications"
        const val CHANNEL_NAME = "Trivia Notifications"
        const val NOTIFICATION_ID = 1
    }

    override fun doWork(): Result {
        Log.d("TriviaNotificationWorker", "doWork called")
        try {
            showNotification()
            return Result.success()
        } catch (e: Exception) {
            Log.e("TriviaNotificationWorker", "Error in doWork", e)
            return Result.failure()
        }
    }

    private fun showNotification() {
        Log.d("TriviaNotificationWorker", "showNotification called")
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Notification for daily trivia"
        notificationManager.createNotificationChannel(channel)

        val notificationIntent = Intent(applicationContext, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Trivia, Let's Go!")
            .setContentText("Make your screen time count. Take a quick trivia!")
            .setSmallIcon(R.drawable.ic_play)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
