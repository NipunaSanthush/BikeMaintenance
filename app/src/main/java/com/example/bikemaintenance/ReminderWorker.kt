package com.example.bikemaintenance

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        checkReminders()
        return Result.success()
    }

    private fun checkReminders() {
        val prefs = applicationContext.getSharedPreferences("bike_reminders", Context.MODE_PRIVATE)
        val licenseDate = prefs.getString("license_date", null)
        val insuranceDate = prefs.getString("insurance_date", null)

        if (licenseDate != null) checkDateAndNotify("License", licenseDate, 1)
        if (insuranceDate != null) checkDateAndNotify("Insurance", insuranceDate, 2)
    }

    private fun checkDateAndNotify(type: String, dateStr: String, notificationId: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val expiryDate = sdf.parse(dateStr)
            val currentDate = Date()

            if (expiryDate != null) {
                val diff = expiryDate.time - currentDate.time
                val daysLeft = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                if (daysLeft in 0..7) {
                    showNotification(
                        "⚠️ $type Expiring Soon!",
                        "Your bike $type expires in $daysLeft days. Renew it now!"
                        , notificationId)
                } else if (daysLeft < 0) {
                    showNotification(
                        "🚨 $type EXPIRED!",
                        "Your bike $type has expired! Do not ride without renewing."
                        , notificationId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNotification(title: String, message: String, notificationId: Int) {
        val context = applicationContext
        val channelId = "bike_reminders_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Bike Reminders", NotificationManager.IMPORTANCE_HIGH)
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }
}