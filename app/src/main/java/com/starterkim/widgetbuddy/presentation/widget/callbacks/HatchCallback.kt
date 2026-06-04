package com.starterkim.widgetbuddy.presentation.widget.callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.starterkim.widgetbuddy.data.petRepository
import com.starterkim.widgetbuddy.domain.PetStateCalculator
import com.starterkim.widgetbuddy.presentation.widget.PetWidget

class HatchCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        context.petRepository.updateStatus { status ->
            PetStateCalculator.hatchPet(status)
        }
        PetWidget().update(context, glanceId)
    }
}
