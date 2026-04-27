package com.starterkim.widgetbuddy.ui.widget.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.action
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.domain.PetType
import com.starterkim.widgetbuddy.ui.widget.callbacks.HatchCallback

@SuppressLint("RestrictedApi")
@Composable
fun PetScreen(
    petState: PetState,
    petName: String,
    petImageRes: Int,
    petImageSize: Dp,
    affectionCount: Int,
    textToShow: String,
    modifier: GlanceModifier = GlanceModifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = GlanceModifier.height(45.dp))

        Column(
            modifier =
                GlanceModifier
                    .height(120.dp)
                    .width(210.dp),
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
                    provider = ImageProvider(petImageRes),
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
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 120)
@Composable
private fun PetScreenPreview() {
    PetScreen(
        petState = PetState.IDLE,
        petName = "겸댕",
        petImageRes = PetType.BAPSAE.idleImage,
        petImageSize = 75.dp,
        affectionCount = 3,
        textToShow = "안녕 개발자",
        modifier = GlanceModifier.fillMaxSize()
    )
}
