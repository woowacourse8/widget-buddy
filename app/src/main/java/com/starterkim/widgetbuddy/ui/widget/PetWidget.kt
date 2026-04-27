package com.starterkim.widgetbuddy.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import com.starterkim.widgetbuddy.data.PetDataStoreKeys
import com.starterkim.widgetbuddy.data.PetStateDefinition
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.domain.PetType
import com.starterkim.widgetbuddy.ui.mapper.PetDialogueMapper
import com.starterkim.widgetbuddy.ui.mapper.PetVisualMapper
import com.starterkim.widgetbuddy.ui.widget.component.LeftTouchArea
import com.starterkim.widgetbuddy.ui.widget.component.PetScreen
import com.starterkim.widgetbuddy.ui.widget.component.RightTouchArea

/**
 * 펫 위젯의 UI 구성을 담당한다.
 */
class PetWidget : GlanceAppWidget() {
    override val stateDefinition = PetStateDefinition

    companion object {
        private const val TAG = "PetWidget"
    }

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            val prefs = currentState<Preferences>()
            PetWidgetContent(prefs = prefs)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun PetWidgetContent(prefs: Preferences?) {
        // --- 1. (데이터 로딩) ---
        val petStateString = prefs?.get(PetDataStoreKeys.PET_STATE) ?: PetState.EGG.name
        val petTypeString = prefs?.get(PetDataStoreKeys.PET_TYPE) ?: PetType.NONE.name
        val petState = PetState.fromString(petStateString)
        val petType = PetType.fromString(petTypeString)
        Log.d(TAG, "Content: petState = $petState, petType = $petType")

        val petName = prefs?.get(PetDataStoreKeys.PET_NAME) ?: "뽀짝이"
        val userName = prefs?.get(PetDataStoreKeys.USER_NAME) ?: "주인님"
        val affectionCount = prefs?.get(PetDataStoreKeys.PET_AFFECTION_COUNT) ?: 0
        val petImageRes = PetVisualMapper.getImageResource(petType, petState)
        val satiety = prefs?.get(PetDataStoreKeys.PET_SATIETY) ?: 100
        val joy = prefs?.get(PetDataStoreKeys.PET_JOY) ?: 100
        val petMessage = prefs?.get(PetDataStoreKeys.PET_MESSAGE) ?: ""
        val textToShow =
            PetDialogueMapper.getDialogue(
                petState,
                satiety,
                joy,
                petName,
                userName,
                petMessage,
            )

        val touchAreaSize = 45.dp
        val petImageSize = 70.dp

        // --- 2. 전체 레이아웃 ---
        PetWidgetContent(
            petState = petState,
            petName = petName,
            petImageRes = petImageRes,
            petImageSize = petImageSize,
            touchAreaSize = touchAreaSize,
            affectionCount = affectionCount,
            textToShow = textToShow
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
            // [A] 배경 이미지
            Image(
                provider = ImageProvider(R.drawable.console_white),
                contentDescription = null,
                modifier = GlanceModifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )

            // [B] UI 배치용 Row (왼쪽 버튼 - 가운데 화면 - 오른쪽 버튼)
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // --- [C] 왼쪽 버튼 (Column): 밥주기 + 놀아주기 ---
                LeftTouchArea(
                    petState = petState,
                    areaSize = touchAreaSize,
                    modifier = GlanceModifier,
                )

                // --- [D] 중간 (Column): 펫 화면 ---
                PetScreen(
                    petState = petState,
                    petName = petName,
                    petImageRes = petImageRes,
                    petImageSize = petImageSize,
                    affectionCount = affectionCount,
                    textToShow = textToShow,
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight()
                )

                // --- [E] 오른쪽 버튼 (Column): 말걸기 + 메인 앱 ---
                RightTouchArea(
                    petState = petState,
                    areaSize = touchAreaSize,
                    modifier = GlanceModifier
                )
            }
        }
    }

    // 펫스크린이 내려와 보이는 것은 신경 안 써도 됨.
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
            textToShow = "안녕"
        )
    }
}
