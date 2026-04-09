package com.starterkim.widgetbuddy.ui.widget.callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.starterkim.widgetbuddy.data.PetDataStoreKeys
import com.starterkim.widgetbuddy.data.dataStore
import com.starterkim.widgetbuddy.domain.PetStateCalculator
import com.starterkim.widgetbuddy.ui.widget.PetWidget

class HatchCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        context.dataStore.updateData { immutablePrefs ->
            val mutablePrefs = immutablePrefs.toMutablePreferences()
            mutablePrefs.clear()
            PetStateCalculator.hatchPet(mutablePrefs)
            mutablePrefs[PetDataStoreKeys.LAST_AFFECTION_UPDATE_DATE] = ""

            mutablePrefs
        }

        PetWidget().update(context, glanceId)
    }
}
