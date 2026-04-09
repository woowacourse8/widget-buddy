package com.starterkim.widgetbuddy.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.starterkim.widgetbuddy.data.dataStore
import com.starterkim.widgetbuddy.domain.PetStateCalculator
import com.starterkim.widgetbuddy.ui.widget.PetWidget

/**
 * WorkManager에 의해 주기적으로 실행되어
 * 펫의 수동적 상태 업데이트를 처리한다.
 */
class PetTickWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Log.d("PetTickWorker", "doWork() 실행됨! 15분경과")

        try {
            context.dataStore.updateData { immutablePrefs ->
                val mutablePrefs = immutablePrefs.toMutablePreferences()
                PetStateCalculator.applyPassiveUpdates(mutablePrefs)
                mutablePrefs
            }

            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceAppWidgetManager.getGlanceIds(PetWidget()::class.java)

            glanceIds.forEach { glanceId ->
                PetWidget().update(context, glanceId)
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("PetTickWorker", "doWork 실패: ${e.message}")
            e.printStackTrace()
            return Result.failure()
        }
    }
}
