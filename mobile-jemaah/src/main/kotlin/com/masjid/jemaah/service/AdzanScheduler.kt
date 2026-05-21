package com.masjid.jemaah.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.masjid.core.domain.DailyAdzanSchedule
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdzanScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAdzan(schedule: DailyAdzanSchedule) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        
        schedule.times.forEach { prayer ->
            if (prayer.name == "Terbit") return@forEach
            
            val prayerTime = sdf.parse("${schedule.date} ${prayer.time}")
            if (prayerTime != null && prayerTime.after(Date())) {
                val intent = Intent(context, AdzanReceiver::class.java).apply {
                    putExtra("PRAYER_NAME", prayer.name)
                }
                
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    prayer.name.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            prayerTime.time,
                            pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        prayerTime.time,
                        pendingIntent
                    )
                }
            }
        }
    }
}
