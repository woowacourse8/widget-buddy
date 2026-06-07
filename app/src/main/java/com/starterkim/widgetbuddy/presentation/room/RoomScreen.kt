package com.starterkim.widgetbuddy.presentation.room

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.domain.PetStatus
import com.starterkim.widgetbuddy.domain.PetType
import com.starterkim.widgetbuddy.presentation.mapper.PetVisualMapper
import com.starterkim.widgetbuddy.presentation.room.component.PetActionButton
import com.starterkim.widgetbuddy.presentation.theme.WidgetBuddyTheme

@Composable
fun RoomScreen(
    petStatus: PetStatus,
    onShowAd: () -> Unit,
    onGiveLoveClick: () -> Unit
) {
    val petIsRunaway = petStatus.hasRunAway
    val petIsEgg = petStatus.isEgg

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = PetVisualMapper.getRoomBackground(petStatus.decorPoints)),
                contentDescription = "Pet House Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

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

        PetActionButton(
            petIsRunaway = petIsRunaway,
            petIsEgg = petIsEgg,
            onShowAd = onShowAd,
            onGiveLoveClick = onGiveLoveClick
        )
    }
}

@Preview(name = "룸 - 일반 상태", showBackground = true)
@Composable
private fun RoomScreenNormalPreview() {
    WidgetBuddyTheme {
        RoomScreen(
            petStatus = PetStatus(type = PetType.BAPSAE, state = PetState.IDLE, decorPoints = 0),
            onShowAd = {},
            onGiveLoveClick = {}
        )
    }
}

@Preview(name = "룸 - 알 상태", showBackground = true)
@Composable
private fun RoomScreenEggPreview() {
    WidgetBuddyTheme {
        RoomScreen(
            petStatus = PetStatus(type = PetType.NONE, state = PetState.EGG),
            onShowAd = {},
            onGiveLoveClick = {}
        )
    }
}

@Preview(name = "룸 - 가출 상태", showBackground = true)
@Composable
private fun RoomScreenRunawayPreview() {
    WidgetBuddyTheme {
        RoomScreen(
            petStatus = PetStatus(
                type = PetType.BAPSAE,
                state = PetState.RUNAWAY,
                decorPoints = 10
            ),
            onShowAd = {},
            onGiveLoveClick = {}
        )
    }
}
