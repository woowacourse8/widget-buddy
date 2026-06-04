package com.starterkim.widgetbuddy.ui.app.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.starterkim.widgetbuddy.ui.MainScreen

@Composable
fun BottomNavigationBar(
    currentScreen: MainScreen,
    onScreenChange: (MainScreen) -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == MainScreen.PET_HOUSE,
            onClick = { onScreenChange(MainScreen.PET_HOUSE) },
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "펫 하우스") },
        )
        NavigationBarItem(
            selected = currentScreen == MainScreen.SETTINGS,
            onClick = { onScreenChange(MainScreen.SETTINGS) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "설정") },
        )
    }
}

@Preview(name = "펫하우스 바텀 프리뷰")
@Composable
private fun BottomNavigationBarPreview1() {
    BottomNavigationBar(
        currentScreen = MainScreen.PET_HOUSE
    ) { }
}

@Preview(name = "세팅 바텀 프리뷰")
@Composable
private fun BottomNavigationBarPreview2() {
    BottomNavigationBar(
        currentScreen = MainScreen.SETTINGS
    ) { }
}
