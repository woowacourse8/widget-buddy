package com.starterkim.widgetbuddy.ui.widget.callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.starterkim.widgetbuddy.data.petRepository
import com.starterkim.widgetbuddy.domain.PetStateCalculator
import com.starterkim.widgetbuddy.ui.widget.PetWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeedCallback : ActionCallback {
    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val repository = context.petRepository

        repository.updateStatus { status ->
            PetStateCalculator.feedPet(status)
        }
        PetWidget().update(context, glanceId)

        scope.launch {
            delay(5000L) // 5초 대기
            repository.updateStatus { status ->
                PetStateCalculator.restoreStateAfterFeedback(status)
            }
            PetWidget().update(context, glanceId)
        }
    }
}
