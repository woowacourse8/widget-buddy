package com.starterkim.widgetbuddy.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
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
import com.starterkim.widgetbuddy.BuildConfig
import com.starterkim.widgetbuddy.R
import com.starterkim.widgetbuddy.data.petRepository
import com.starterkim.widgetbuddy.presentation.theme.WidgetBuddyTheme
import com.starterkim.widgetbuddy.presentation.widget.PetWidget
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var mRewardedAd: RewardedAd? = null

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(petRepository) as T
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private val AD_UNIT_ID =
            if (BuildConfig.DEBUG) {
                "ca-app-pub-3940256099942544/5224354917"
            } else {
                "ca-app-pub-4729200165720419/7331412876"
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        loadRewardedAd()

        setContent {
            WidgetBuddyTheme {
                val status by viewModel.petStatus.collectAsState()

                LaunchedEffect(status.language) {
                    val appLocales = AppCompatDelegate.getApplicationLocales()
                    if (appLocales.toLanguageTags() != status.language) {
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(status.language),
                        )
                    }
                }

                MainAppScreen(
                    status = status,
                    onShowAd = { showAdAndBringPetBack() },
                    onGiveLoveClick = {
                        viewModel.giveLoveAndGetPoints { newPoints ->
                            lifecycleScope.launch {
                                PetWidget().updateAll(this@MainActivity)
                            }
                            if (newPoints != null) {
                                val msg =
                                    when (newPoints) {
                                        5 -> getString(R.string.love_reward_carpet)
                                        10 -> getString(R.string.love_reward_fireplace)
                                        20 -> getString(R.string.love_reward_sofa)
                                        else -> getString(R.string.love_reward_points, newPoints)
                                    }
                                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                            } else {
                                Toast
                                    .makeText(
                                        this@MainActivity,
                                        getString(R.string.already_gave_love, status.decorPoints),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                        }
                    },
                    onSavePetName = { name ->
                        viewModel.updatePetName(name)
                        lifecycleScope.launch { PetWidget().updateAll(this@MainActivity) }
                    },
                    onSaveUserName = { name ->
                        viewModel.updateUserName(name)
                        lifecycleScope.launch { PetWidget().updateAll(this@MainActivity) }
                    },
                    onLanguageChange = { langCode -> changeLanguage(langCode) },
                )
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

    /**
     * 앱과 위젯의 언어를 동시에 변경한다.
     * DataStore 쓰기 → 위젯 갱신 → 앱 locale 적용 순서로 진행해
     * 액티비티 재생성 후에도 위젯과 앱이 같은 언어로 일관되게 보이도록 한다.
     */
    private fun changeLanguage(langCode: String) {
        val appContext = applicationContext
        lifecycleScope.launch {
            appContext.petRepository.updateStatus { it.copy(language = langCode) }
            PetWidget().updateAll(appContext)
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(langCode),
            )
        }
    }
}
