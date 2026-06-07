package com.starterkim.widgetbuddy.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import com.starterkim.widgetbuddy.R
import com.starterkim.widgetbuddy.data.PetStateDefinition
import com.starterkim.widgetbuddy.data.localizedFor
import com.starterkim.widgetbuddy.data.toPetStatus
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.domain.PetType
import com.starterkim.widgetbuddy.presentation.mapper.PetDialogueMapper
import com.starterkim.widgetbuddy.presentation.mapper.PetVisualMapper
import com.starterkim.widgetbuddy.presentation.widget.component.LeftTouchArea
import com.starterkim.widgetbuddy.presentation.widget.component.PetScreen
import com.starterkim.widgetbuddy.presentation.widget.component.RightTouchArea

class PetWidget : GlanceAppWidget() {
    override val stateDefinition = PetStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            val prefs = currentState<Preferences>()
            PetWidgetContent(context, prefs = prefs)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun PetWidgetContent(
        context: Context,
        prefs: Preferences?,
    ) {
        val status =
            prefs?.toPetStatus(context) ?: com.starterkim.widgetbuddy.domain
                .PetStatus()
        val localizedContext = context.localizedFor(status.language)

        val textToShow =
            PetDialogueMapper.getDialogue(
                localizedContext,
                status.state,
                status.satiety,
                status.joy,
                status.getDisplayName(localizedContext),
                status.getDisplayUserName(localizedContext),
                status.message,
            )

        val touchAreaSize = 45.dp
        val petImageSize = 70.dp
        val petImageRes = PetVisualMapper.getImageResource(status.type, status.state)

        PetWidgetContent(
            petState = status.state,
            petName = status.getDisplayName(localizedContext),
            petImageRes = petImageRes,
            petImageSize = petImageSize,
            touchAreaSize = touchAreaSize,
            affectionCount = status.affectionCount,
            textToShow = textToShow,
        )
    }

    @Composable
    private fun PetWidgetContent(
        petState: PetState,
        petName: String,
        petImageRes: Int,
        petImageSize: Dp,
        touchAreaSize: Dp,
        affectionCount: Int,
        textToShow: String,
    ) {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                provider = ImageProvider(R.drawable.console_white),
                contentDescription = null,
                modifier = GlanceModifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )

            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LeftTouchArea(
                    petState = petState,
                    areaSize = touchAreaSize,
                    modifier = GlanceModifier,
                )

                PetScreen(
                    petState = petState,
                    petName = petName,
                    petImageRes = petImageRes,
                    petImageSize = petImageSize,
                    affectionCount = affectionCount,
                    textToShow = textToShow,
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                )

                RightTouchArea(
                    petState = petState,
                    areaSize = touchAreaSize,
                    modifier = GlanceModifier,
                )
            }
        }
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 360, heightDp = 220)
    @Composable
    private fun PetWidgetContentPreview() {
        val petType = PetType.BAPSAE
        val petState = PetState.IDLE
        val petImageRes = PetVisualMapper.getImageResource(petType, petState)

        PetWidgetContent(
            petState = PetState.IDLE,
            petName = "지우",
            petImageRes = petImageRes,
            petImageSize = 70.dp,
            touchAreaSize = 45.dp,
            affectionCount = 5,
            textToShow = "안녕",
        )
    }
}
