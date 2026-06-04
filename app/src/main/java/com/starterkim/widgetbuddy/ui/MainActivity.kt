package com.starterkim.widgetbuddy.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.starterkim.widgetbuddy.data.petRepository
import com.starterkim.widgetbuddy.domain.PetStatus
import com.starterkim.widgetbuddy.ui.app.component.BottomNavigationBar
import com.starterkim.widgetbuddy.ui.room.RoomScreen
import com.starterkim.widgetbuddy.ui.theme.WidgetBuddyTheme
import com.starterkim.widgetbuddy.ui.widget.PetWidget
import kotlinx.coroutines.launch

enum class MainScreen {
    PET_HOUSE, SETTINGS,
}

class MainActivity : ComponentActivity() {
    private var mRewardedAd: RewardedAd? = null

    // Hilt 대신 사용하는 간단한 ViewModel 생성 방식
    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(petRepository) as T
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val AD_UNIT_ID = "ca-app-pub-4729200165720419/7331412876"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        loadRewardedAd()

        setContent {
            WidgetBuddyTheme {
                val status by viewModel.petStatus.collectAsState()
                MainAppScreen(status)
            }
        }
    }

    // --- 광고 로직 ---
    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.toString())
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mRewardedAd = rewardedAd
                }
            },
        )
    }

    private fun showAdAndBringPetBack() {
        mRewardedAd?.let { ad ->
            ad.show(this) { _ ->
                Log.d(TAG, "User earned the reward.")
                bringPetBackAfterAd()
            }
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            Toast.makeText(this, "광고 로드 중.. 잠시 후 다시 시도하세요.", Toast.LENGTH_LONG).show()
        }
    }

    private fun bringPetBackAfterAd() {
        viewModel.bringPetBack()
        lifecycleScope.launch {
            PetWidget().updateAll(this@MainActivity)
        }
        Toast.makeText(this, "펫이 돌아왔습니다!", Toast.LENGTH_SHORT).show()
        loadRewardedAd()
    }

    // --- Composable 영역 ---
    @Composable
    fun MainAppScreen(status: PetStatus) {
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
                    MainScreen.PET_HOUSE -> RoomScreen(
                        petStatus = status,
                        onShowAd = { showAdAndBringPetBack() },
                        onGiveLoveClick = {
                            viewModel.giveLoveAndGetPoints { message ->
                                lifecycleScope.launch {
                                    PetWidget().updateAll(this@MainActivity)
                                }
                                if (message != null) {
                                    Toast.makeText(
                                        this@MainActivity, message, Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "오늘은 이미 사랑을 줬어요. (총 ${status.decorPoints} P)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        })

                    MainScreen.SETTINGS -> SettingsScreen(status)
                }
            }
        }
    }

    @Composable
    fun SettingsScreen(status: PetStatus) {
        var petNameInput by remember { mutableStateOf("") }
        var userNameInput by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "현재 설정",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text("펫 이름: ${status.name}, 주인님 이름: ${status.userName}")
            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "펫의 새 이름을 지어주세요!", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = petNameInput,
                onValueChange = { petNameInput = it },
                label = { Text("새 펫 이름 입력") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (petNameInput.isNotBlank()) {
                    viewModel.updatePetName(petNameInput)
                    lifecycleScope.launch { PetWidget().updateAll(this@MainActivity) }
                    Toast.makeText(this@MainActivity, "펫 이름 저장 완료!", Toast.LENGTH_SHORT).show()
                    petNameInput = ""
                }
            }) {
                Text("펫 이름 저장하기")
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(text = "주인님의 이름을 알려주세요!", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = userNameInput,
                onValueChange = { userNameInput = it },
                label = { Text("주인님 이름 입력") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (userNameInput.isNotBlank()) {
                    viewModel.updateUserName(userNameInput)
                    lifecycleScope.launch { PetWidget().updateAll(this@MainActivity) }
                    Toast.makeText(this@MainActivity, "주인님 이름 저장 완료!", Toast.LENGTH_SHORT).show()
                    userNameInput = ""
                }
            }) {
                Text("주인님 이름 저장하기")
            }
        }
    }
}
