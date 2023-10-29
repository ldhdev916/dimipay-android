package com.ldhdev.dimipay

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

fun Context.scheduleDimipayAlarm() {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val times = listOf(
        LocalTime.of(13, 0),
        LocalTime.of(18, 50),
    )

    val now = LocalDateTime.now()

    for (time in times) {
        var at = now.with(time)

        if (at.isBefore(now)) {
            at = at.plusDays(1)
        }

        while (at.dayOfWeek == DayOfWeek.SATURDAY || at.dayOfWeek == DayOfWeek.SUNDAY) {
            at = at.plusDays(1)
        }

        val pendingIntent = PendingIntent.getForegroundService(
            this,
            time.hour * 100 + time.minute,
            Intent(this, ShakeDetectionService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = at.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
}