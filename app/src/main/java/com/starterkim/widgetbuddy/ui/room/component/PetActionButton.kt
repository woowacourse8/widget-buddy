package com.starterkim.widgetbuddy.ui.room.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PetActionButton(
    petIsRunaway: Boolean,
    petIsEgg: Boolean,
    onShowAd: () -> Unit,
    onGiveLoveClick: () -> Unit
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (petIsRunaway) {
            Button(onClick = onShowAd) {
                Text("[광고 시청] 펫 다시 데려오기")
            }
        } else if (petIsEgg) {
            Text("아직 알 상태입니다. 위젯에서 부화시켜주세요!", color = Color.Gray)
        } else {
            Button(onClick = onGiveLoveClick) {
                Text("사랑 주기 ❤️ (포인트 +1)")
            }
        }
    }
}

@Preview(name = "펫 가출", showBackground = true)
@Composable
private fun PetActionButtonPreview1() {
    PetActionButton(
        petIsRunaway = true,
        petIsEgg = false,
        onShowAd = {},
        onGiveLoveClick = {}
    )
}

@Preview(name = "펫 알 상태", showBackground = true)
@Composable
private fun PetActionButtonPreview2() {
    PetActionButton(
        petIsRunaway = false,
        petIsEgg = true,
        onShowAd = {},
        onGiveLoveClick = {}
    )
}
