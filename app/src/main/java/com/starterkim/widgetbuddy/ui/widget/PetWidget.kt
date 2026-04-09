package com.starterkim.widgetbuddy.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.starterkim.widgetbuddy.R
import com.starterkim.widgetbuddy.data.PetDataStoreKeys
import com.starterkim.widgetbuddy.data.PetStateDefinition
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.domain.PetType
import com.starterkim.widgetbuddy.ui.MainActivity
import com.starterkim.widgetbuddy.ui.mapper.PetDialogueMapper
import com.starterkim.widgetbuddy.ui.mapper.PetVisualMapper
import com.starterkim.widgetbuddy.ui.widget.callbacks.FeedCallback
import com.starterkim.widgetbuddy.ui.widget.callbacks.HatchCallback
import com.starterkim.widgetbuddy.ui.widget.callbacks.PlayCallback
import com.starterkim.widgetbuddy.ui.widget.callbacks.TalkCallback

/**
 * 펫 위젯의 UI 구성을 담당한다.
 */
class PetWidget : GlanceAppWidget() {
    override val stateDefinition = PetStateDefinition

    // [수정 1] ktlint 에러 방지를 위해 TAG를 companion object의 상수로 변경
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

    // [수정 2] ktlint 함수명 에러(대문자 시작)를 피하기 위해 Content -> petContent 로 변경
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
        val imageRes = PetVisualMapper.getImageResource(petType, petState)
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

        // [수정 3] BackgroundModifier.Color.Companion... 같이 불필요하게 길고 에러를 유발하는 코드 정리
        val isDebug = false
        val debugButtonColor = if (isDebug) Color.Red.copy(alpha = 0.3f) else Color.Transparent
        val debugBtnColor = if (isDebug) Color.Black.copy(alpha = 0.3f) else Color.Transparent
        val debugScreenColor = if (isDebug) Color.White.copy(alpha = 0.9f) else Color.Transparent

        val buttonSize = 45.dp
        val petImageSize = 70.dp

        // --- 2. 전체 레이아웃 ---
        // [수정 4] GlanceModifier.Companion, Alignment.Companion 등 불필요한 .Companion 호출 모두 제거
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
                Column(
                    modifier =
                        GlanceModifier
                            .padding(start = 14.dp, end = 7.dp, top = 13.dp, bottom = 13.dp)
                            .background(debugButtonColor),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // (1) 밥주기 (왼쪽 위)
                    Box(
                        modifier =
                            GlanceModifier
                                .size(buttonSize)
                                .background(debugBtnColor)
                                .clickable(
                                    if (petState != PetState.EGG && petState != PetState.RUNAWAY) {
                                        actionRunCallback<FeedCallback>()
                                    } else {
                                        action { }
                                    },
                                ),
                    ) { }

                    Spacer(modifier = GlanceModifier.height(14.dp))

                    // (2) 놀아주기 (왼쪽 아래)
                    Box(
                        modifier =
                            GlanceModifier
                                .size(buttonSize)
                                .background(debugBtnColor)
                                .clickable(
                                    if (petState != PetState.EGG && petState != PetState.RUNAWAY) {
                                        actionRunCallback<PlayCallback>()
                                    } else {
                                        action { }
                                    },
                                ),
                    ) {}
                }

                // --- [D] 중간 (Column): 펫 화면 ---
                Column(
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = GlanceModifier.height(45.dp))

                    Column(
                        modifier =
                            GlanceModifier
                                .height(120.dp)
                                .width(210.dp)
                                .background(debugScreenColor),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // (알일 때는 이름 숨기기)
                        if (petState != PetState.EGG) {
                            Text(
                                text = petName,
                                style =
                                    TextStyle(
                                        color = ColorProvider(Color.White),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                    ),
                                modifier = GlanceModifier.padding(bottom = 4.dp),
                            )
                        }

                        Box(
                            modifier = GlanceModifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                provider = ImageProvider(imageRes),
                                contentDescription = petState.name,
                                modifier =
                                    GlanceModifier
                                        .size(petImageSize)
                                        .clickable(
                                            if (petState == PetState.EGG) {
                                                actionRunCallback<HatchCallback>()
                                            } else {
                                                action { }
                                            },
                                        ),
                            )

                            // 2. 하트 표시: 펫 이미지 위에 겹쳐서 오른쪽 상단에 배치
                            if (petState != PetState.EGG) {
                                Row(
                                    modifier =
                                        GlanceModifier
                                            .padding(
                                                start = petImageSize * 2f,
                                                bottom = petImageSize * 0.4f,
                                            ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "❤️$affectionCount",
                                        style =
                                            TextStyle(
                                                color = ColorProvider(Color.White),
                                            ),
                                        modifier = GlanceModifier.padding(end = 0.dp),
                                    )
                                }
                            }
                        }

                        // (대사)
                        Text(
                            text = textToShow,
                            style =
                                TextStyle(
                                    color = ColorProvider(Color.Black),
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                ),
                            modifier =
                                GlanceModifier
                                    .padding(horizontal = 4.dp)
                                    .background(Color.White.copy(0.8f)),
                        )
                    }
                }

                // --- [E] 오른쪽 버튼 (Column): 말걸기 + 메인 앱 ---
                Column(
                    modifier =
                        GlanceModifier
                            .padding(top = 15.dp, bottom = 15.dp, start = 2.dp, end = 16.dp)
                            .background(debugButtonColor),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // (1) 말걸기 (오른쪽 위)
                    Box(
                        modifier =
                            GlanceModifier
                                .size(buttonSize)
                                .background(debugBtnColor)
                                .clickable(
                                    if (petState != PetState.EGG && petState != PetState.RUNAWAY) {
                                        actionRunCallback<TalkCallback>()
                                    } else {
                                        action { }
                                    },
                                ),
                    ) {}

                    Spacer(modifier = GlanceModifier.height(14.dp))

                    // (2) 메인 앱 방문 (오른쪽 아래)
                    Box(
                        modifier =
                            GlanceModifier
                                .size(buttonSize)
                                .background(debugBtnColor)
                                .clickable(
                                    actionStartActivity<MainActivity>(),
                                ),
                    ) {}
                }
            }
        }
    }
}
