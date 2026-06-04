package com.starterkim.widgetbuddy.presentation.widget.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.action
import androidx.glance.action.actionStartActivity
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
import com.starterkim.widgetbuddy.presentation.MainActivity
import com.starterkim.widgetbuddy.presentation.widget.callbacks.TalkCallback

@Composable
fun RightTouchArea(
    petState: PetState,
    areaSize: Dp,
    modifier: GlanceModifier = GlanceModifier,
    showTouchArea: Boolean = false,
) {
    val debugAreaColor = if (showTouchArea) Color.Red.copy(alpha = 0.5f) else Color.Transparent
    val areaModifier = GlanceModifier.size(areaSize).background(debugAreaColor)

    Column(
        modifier = modifier
            .padding(top = 15.dp, bottom = 15.dp, start = 2.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TalkTouchArea(
            petState = petState,
            modifier = areaModifier
        )

        Spacer(modifier = GlanceModifier.height(14.dp))

        VisitAppTouchArea(
            modifier = areaModifier
        )
    }
}

@Composable
private fun TalkTouchArea(
    petState: PetState,
    modifier: GlanceModifier = GlanceModifier,
) {
    // 버튼 형태가 아니라 특정 영역을 클릭했을 때 동작하고 싶어서 Box 사용
    Box(
        modifier = modifier
            .clickable(
                if (petState != PetState.EGG && petState != PetState.RUNAWAY) {
                    actionRunCallback<TalkCallback>()
                } else {
                    action { }
                },
            ),
    ) {}
}

@Composable
private fun VisitAppTouchArea(
    modifier: GlanceModifier = GlanceModifier
) {
    Box(
        modifier = modifier.clickable(
            actionStartActivity<MainActivity>(),
        ),
    ) {}
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RightTouchAreaPreview() {
    RightTouchArea(
        petState = PetState.IDLE,
        areaSize = 45.dp,
        showTouchArea = true
    )
}
