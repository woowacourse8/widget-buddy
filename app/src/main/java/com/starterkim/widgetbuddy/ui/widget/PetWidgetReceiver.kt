package com.starterkim.widgetbuddy.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.starterkim.widgetbuddy.worker.PetTickWorker
import java.util.concurrent.TimeUnit

/**
 * 위젯의 생명주기 이벤트를 수신하고
 * GlanceAppWidget 인스턴스를 제공한다.
 */
class PetWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PetWidget()

    override fun onEnabled(context: Context) {
        val tickWorkRequest =
            PeriodicWorkRequestBuilder<PetTickWorker>(
                15,
                TimeUnit.MINUTES,
            ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            tickWorkRequest,
        )
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    companion object {
        private const val WORK_NAME = "pet_tick_work"
    }
}
