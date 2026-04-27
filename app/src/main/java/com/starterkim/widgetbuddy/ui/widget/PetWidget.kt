package com.starterkim.widgetbuddy.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
            petContent(prefs = prefs)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    fun petContent(prefs: Preferences?) {
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

        val isDebug = false
        val debugScreenColor = if (isDebug) Color.White.copy(alpha = 0.9f) else Color.Transparent

        val buttonSize = 45.dp
        val petImageSize = 70.dp

        // --- 2. 전체 레이아웃 ---
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
                    areaSize = buttonSize,
                    modifier = GlanceModifier.fillMaxHeight()
                )

                // --- [D] 중간 (Column): 펫 화면 ---
                PetScreen(
                    petState = petState,
                    petName = petName,
                    petImageRes = petImageRes,
                    petImageSize = petImageSize,
                    affectionCount = affectionCount,
                    textToShow = textToShow,
                    modifier = GlanceModifier.fillMaxHeight()
                )

                // --- [E] 오른쪽 버튼 (Column): 말걸기 + 메인 앱 ---
                RightTouchArea(
                    petState = petState,
                    areaSize = buttonSize,
                    modifier = GlanceModifier.fillMaxHeight()
                )
            }
        }
    }
}
