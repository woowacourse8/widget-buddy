package com.starterkim.widgetbuddy.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.starterkim.widgetbuddy.data.petRepository
import com.starterkim.widgetbuddy.domain.PetStateCalculator
import com.starterkim.widgetbuddy.ui.widget.PetWidget

class PetTickWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            context.petRepository.updateStatus { status ->
                PetStateCalculator.applyPassiveUpdates(status)
            }

            // 모든 위젯 업데이트 알림
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(PetWidget::class.java)
            glanceIds.forEach { glanceId ->
                PetWidget().update(context, glanceId)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
