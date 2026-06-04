package com.starterkim.widgetbuddy.presentation

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
import androidx.compose.ui.res.stringResource
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
import com.starterkim.widgetbuddy.R
import com.starterkim.widgetbuddy.data.petRepository
import com.starterkim.widgetbuddy.domain.PetStatus
import com.starterkim.widgetbuddy.presentation.common.BottomNavigationBar
import com.starterkim.widgetbuddy.presentation.room.RoomScreen
import com.starterkim.widgetbuddy.presentation.common.theme.WidgetBuddyTheme
import com.starterkim.widgetbuddy.presentation.widget.PetWidget
import kotlinx.coroutines.launch

enum class MainScreen {
    PET_HOUSE,
    SETTINGS,
}

class MainActivity : ComponentActivity() {
    private var mRewardedAd: RewardedAd? = null

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
            Toast.makeText(this, getString(R.string.ad_loading_try_later), Toast.LENGTH_LONG).show()
        }
    }

    private fun bringPetBackAfterAd() {
        viewModel.bringPetBack()
        lifecycleScope.launch {
            PetWidget().updateAll(this@MainActivity)
        }
        Toast.makeText(this, getString(R.string.pet_returned), Toast.LENGTH_SHORT).show()
        loadRewardedAd()
    }

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
                    MainScreen.PET_HOUSE ->
                        RoomScreen(
                            petStatus = status,
                            onShowAd = { showAdAndBringPetBack() },
                            onGiveLoveClick = {
                                viewModel.giveLoveAndGetPoints { message ->
                                    lifecycleScope.launch {
                                        PetWidget().updateAll(this@MainActivity)
                                    }
                                    if (message != null) {
                                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(this@MainActivity, getString(R.string.already_gave_love, status.decorPoints), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )

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
                text = stringResource(R.string.current_settings),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(stringResource(R.string.pet_status_info, status.name, status.userName))
            Spacer(modifier = Modifier.height(32.dp))

            Text(text = stringResource(R.string.input_new_pet_name_title), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = petNameInput,
                onValueChange = { petNameInput = it },
                label = { Text(stringResource(R.string.new_pet_name_label)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (petNameInput.isNotBlank()) {
                    viewModel.updatePetName(petNameInput)
                    lifecycleScope.launch { PetWidget().updateAll(this@MainActivity) }
                    Toast.makeText(this@MainActivity, getString(R.string.pet_name_save_complete), Toast.LENGTH_SHORT).show()
                    petNameInput = ""
                }
            }) {
                Text(stringResource(R.string.save_pet_name))
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(text = stringResource(R.string.input_user_name_title), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = userNameInput,
                onValueChange = { userNameInput = it },
                label = { Text(stringResource(R.string.user_name_label)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (userNameInput.isNotBlank()) {
                    viewModel.updateUserName(userNameInput)
                    lifecycleScope.launch { PetWidget().updateAll(this@MainActivity) }
                    Toast.makeText(this@MainActivity, getString(R.string.user_name_save_complete), Toast.LENGTH_SHORT).show()
                    userNameInput = ""
                }
            }) {
                Text(stringResource(R.string.save_user_name))
            }
        }
    }
}
