package com.starterkim.widgetbuddy.ui.room

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.starterkim.widgetbuddy.domain.PetStatus
import com.starterkim.widgetbuddy.ui.mapper.PetVisualMapper
import com.starterkim.widgetbuddy.ui.room.component.PetActionButton

@Composable
fun RoomScreen(
    petStatus: PetStatus,
    onShowAd: () -> Unit,
    onGiveLoveClick: () -> Unit
) {
    val petIsRunaway = petStatus.hasRunAway
    val petIsEgg = petStatus.isEgg

    Column(modifier = Modifier.fillMaxSize()) {
        // 1. 펫의 방
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            // [A] 배경 이미지 (포인트에 따라 변경)
            Image(
                painter = painterResource(id = PetVisualMapper.getRoomBackground(petStatus.decorPoints)),
                contentDescription = "Pet House Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            // [B] 꾸미기 포인트 표시
            Row(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.extraSmall,
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "✨ ${petStatus.decorPoints} P",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // [C] 펫 이미지
            Image(
                painter =
                    painterResource(
                        id =
                            PetVisualMapper.getImageResource(
                                petStatus.type,
                                petStatus.state,
                            ),
                    ),
                contentDescription = "Pet",
                modifier = Modifier.size(120.dp),
            )
        }

        // 2. 컨트롤러 (사랑 주기 / 가출 복귀 버튼)
        PetActionButton(
            petIsRunaway = petIsRunaway,
            petIsEgg = petIsEgg,
            onShowAd = onShowAd,
            onGiveLoveClick = onGiveLoveClick
        )
    }
}
