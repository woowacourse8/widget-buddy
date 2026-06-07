package com.starterkim.widgetbuddy.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.domain.PetStatus
import com.starterkim.widgetbuddy.domain.PetType
import com.starterkim.widgetbuddy.presentation.common.BottomNavigationBar
import com.starterkim.widgetbuddy.presentation.room.RoomScreen
import com.starterkim.widgetbuddy.presentation.settings.SettingsScreen
import com.starterkim.widgetbuddy.presentation.theme.WidgetBuddyTheme

enum class MainScreen {
    PET_HOUSE,
    SETTINGS,
}

@Composable
fun MainAppScreen(
    status: PetStatus,
    onShowAd: () -> Unit,
    onGiveLoveClick: () -> Unit,
    onSavePetName: (String) -> Unit,
    onSaveUserName: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
) {
    var currentScreen by remember { mutableStateOf(MainScreen.PET_HOUSE) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onScreenChange = { currentScreen = it },
            )
        },
    ) { paddingValues ->
        Box(
            Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when (currentScreen) {
                MainScreen.PET_HOUSE ->
                    RoomScreen(
                        petStatus = status,
                        onShowAd = onShowAd,
                        onGiveLoveClick = onGiveLoveClick,
                    )

                MainScreen.SETTINGS ->
                    SettingsScreen(
                        status = status,
                        onSavePetName = onSavePetName,
                        onSaveUserName = onSaveUserName,
                        onLanguageChange = onLanguageChange,
                    )
            }
        }
    }
}

@Preview(name = "메인 - 펫 하우스", showBackground = true)
@Composable
private fun MainAppScreenPetHousePreview() {
    WidgetBuddyTheme {
        MainAppScreen(
            status = PetStatus(type = PetType.BAPSAE, state = PetState.IDLE),
            onShowAd = {},
            onGiveLoveClick = {},
            onSavePetName = {},
            onSaveUserName = {},
            onLanguageChange = {},
        )
    }
}
