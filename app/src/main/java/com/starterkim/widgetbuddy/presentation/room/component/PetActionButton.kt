package com.starterkim.widgetbuddy.presentation.room.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.starterkim.widgetbuddy.R
import com.starterkim.widgetbuddy.presentation.theme.WidgetBuddyTheme

@Composable
fun PetActionButton(
    petIsRunaway: Boolean,
    petIsEgg: Boolean,
    onShowAd: () -> Unit,
    onGiveLoveClick: () -> Unit
) {
    if (petIsRunaway) {
        Button(
            onClick = onShowAd,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(stringResource(R.string.action_bring_pet_back), fontSize = 18.sp)
        }
    } else if (petIsEgg) {
        Button(
            onClick = { },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(stringResource(R.string.action_still_egg), fontSize = 14.sp)
        }
    } else {
        Button(
            onClick = onGiveLoveClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(stringResource(R.string.action_give_love), fontSize = 18.sp)
        }
    }
}

@Preview(name = "애정 주기 버튼")
@Composable
private fun PetActionButtonNormalPreview() {
    WidgetBuddyTheme {
        PetActionButton(petIsRunaway = false, petIsEgg = false, onShowAd = {}, onGiveLoveClick = {})
    }
}

@Preview(name = "알 상태 버튼")
@Composable
private fun PetActionButtonEggPreview() {
    WidgetBuddyTheme {
        PetActionButton(petIsRunaway = false, petIsEgg = true, onShowAd = {}, onGiveLoveClick = {})
    }
}

@Preview(name = "펫 가출 버튼")
@Composable
private fun PetActionButtonRunawayPreview() {
    WidgetBuddyTheme {
        PetActionButton(petIsRunaway = true, petIsEgg = false, onShowAd = {}, onGiveLoveClick = {})
    }
}
