package com.starterkim.widgetbuddy.presentation.widget.callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.starterkim.widgetbuddy.data.localizedFor
import com.starterkim.widgetbuddy.data.petRepository
import com.starterkim.widgetbuddy.domain.PetStateCalculator
import com.starterkim.widgetbuddy.presentation.mapper.PetDialogueMapper
import com.starterkim.widgetbuddy.presentation.widget.PetWidget

class TalkCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        context.petRepository.updateStatus { status ->
            val updated = PetStateCalculator.checkAndGrantDailyAffection(status)
            val localizedContext = context.localizedFor(updated.language)
            val dialogue = PetDialogueMapper.getDialogue(
                localizedContext,
                updated.state,
                updated.satiety,
                updated.joy,
                updated.getDisplayName(localizedContext),
                updated.getDisplayUserName(localizedContext),
                "",
            )
            updated.copy(message = dialogue, lastTalkTimestamp = System.currentTimeMillis())
        }
        PetWidget().update(context, glanceId)
    }
}
