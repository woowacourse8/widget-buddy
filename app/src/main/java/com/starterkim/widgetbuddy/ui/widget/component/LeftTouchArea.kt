package com.starterkim.widgetbuddy.ui.widget.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.action
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.ui.widget.callbacks.FeedCallback
import com.starterkim.widgetbuddy.ui.widget.callbacks.PlayCallback

// 프리뷰 못 봄
@Composable
fun LeftTouchArea(
    petState: PetState,
    areaSize: Dp,
    modifier: GlanceModifier = GlanceModifier
) {
    Column(
        modifier = modifier
            .padding(start = 14.dp, end = 7.dp, top = 15.dp, bottom = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FeedTouchArea(petState, GlanceModifier.size(areaSize))

        Spacer(modifier = GlanceModifier.height(14.dp))

        PlayTouchArea(petState, GlanceModifier.size(areaSize))
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
