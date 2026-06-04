package com.starterkim.widgetbuddy.presentation.widget.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.action
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.presentation.widget.callbacks.FeedCallback
import com.starterkim.widgetbuddy.presentation.widget.callbacks.PlayCallback

@Composable
fun LeftTouchArea(
    petState: PetState,
    areaSize: Dp,
    modifier: GlanceModifier = GlanceModifier,
    showTouchArea: Boolean = false,
) {
    val debugAreaColor = if (showTouchArea) Color.Red.copy(alpha = 0.5f) else Color.Transparent
    val areaModifier = GlanceModifier.size(areaSize).background(debugAreaColor)

    Column(
        modifier = modifier
            .padding(start = 14.dp, end = 7.dp, top = 15.dp, bottom = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FeedTouchArea(petState, areaModifier)

        Spacer(modifier = GlanceModifier.height(14.dp))

        PlayTouchArea(petState, areaModifier)
    }
}

@Composable
private fun FeedTouchArea(
    petState: PetState,
    modifier: GlanceModifier = GlanceModifier
) {
    Box(
        modifier = modifier
            .clickable(
                if (petState != PetState.EGG && petState != PetState.RUNAWAY) {
                    actionRunCallback<FeedCallback>()
                } else {
                    action { }
                },
            ),
    ) { }
}

@Composable
private fun PlayTouchArea(
    petState: PetState,
    modifier: GlanceModifier,
) {
    Box(
        modifier = modifier
            .clickable(
                if (petState != PetState.EGG && petState != PetState.RUNAWAY) {
                    actionRunCallback<PlayCallback>()
                } else {
                    action { }
                },
            ),
    ) {}
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun LeftTouchAreaPreview() {
    LeftTouchArea(
        petState = PetState.IDLE,
        areaSize = 45.dp,
        showTouchArea = true
    )
}
