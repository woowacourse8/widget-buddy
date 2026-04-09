package com.starterkim.widgetbuddy.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.edit
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.starterkim.widgetbuddy.data.PetDataStoreKeys
import com.starterkim.widgetbuddy.data.dataStore
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.domain.PetStateCalculator
import com.starterkim.widgetbuddy.domain.PetType
import com.starterkim.widgetbuddy.ui.mapper.PetVisualMapper
import com.starterkim.widgetbuddy.ui.theme.WidgetBuddyTheme
import com.starterkim.widgetbuddy.ui.widget.PetWidget
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class MainScreen {
    PET_HOUSE,
    SETTINGS,
}

class MainActivity : ComponentActivity() {
    private var mRewardedAd: RewardedAd? = null

    companion object {
        private const val TAG = "MainActivity"
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    @SuppressLint("FlowOperatorInvokedInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        loadRewardedAd()

        setContent {
            WidgetBuddyTheme {
                val petState by dataStore.data
                    .map {
                        PetState.fromString(it[PetDataStoreKeys.PET_STATE])
                    }.collectAsState(initial = PetState.EGG)

                val petType by dataStore.data
                    .map {
                        PetType.fromString(it[PetDataStoreKeys.PET_TYPE])
                    }.collectAsState(initial = PetType.NONE)

                val decorPoints by dataStore.data
                    .map {
                        it[PetDataStoreKeys.DECOR_POINTS] ?: 0
                    }.collectAsState(initial = 0)

                mainAppScreen(petState, petType, decorPoints)
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

    private fun showAdAndBringPetBack(context: Context) {
        mRewardedAd?.let { ad ->
            ad.show(this) { _ ->
                Log.d(TAG, "User earned the reward.")
                bringPetBackAfterAd(context)
            }
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            Toast
                .makeText(
                    context,
                    "광고 로드 중.. 잠시 후 다시 시도하세요.",
                    Toast.LENGTH_LONG,
                ).show()
        }
    }

    private fun bringPetBackAfterAd(context: Context) {
        lifecycleScope.launch {
            context.dataStore.updateData { prefs ->
                PetStateCalculator.bringPetBack(prefs.toMutablePreferences())
            }
            PetWidget().updateAll(context)
            Toast.makeText(context, "펫이 돌아왔습니다!", Toast.LENGTH_SHORT).show()

            loadRewardedAd()
        }
    }

    // --- 포인트 및 사랑 주기 로직 ---
    private suspend fun giveLoveAndGetPoints(context: Context): Pair<Int, Boolean> {
        var finalDecorPoints = 0
        var didPointsIncrease = false

        context.dataStore.updateData { immutablePrefs ->
            val currentState = PetState.fromString(immutablePrefs[PetDataStoreKeys.PET_STATE])

            if (currentState == PetState.EGG || currentState == PetState.RUNAWAY) {
                finalDecorPoints = immutablePrefs[PetDataStoreKeys.DECOR_POINTS] ?: 0
                return@updateData immutablePrefs
            }

            val mutablePrefs = immutablePrefs.toMutablePreferences()

            mutablePrefs[PetDataStoreKeys.LAST_MAIN_APP_VISIT_TIMESTAMP] =
                System.currentTimeMillis()
            mutablePrefs[PetDataStoreKeys.PET_STATE] = PetState.IDLE.name

            val today = LocalDate.now().toString()

            val lastDecorDate = mutablePrefs[PetDataStoreKeys.LAST_DECOR_POINT_DATE] ?: ""
            if (today != lastDecorDate) {
                didPointsIncrease = true
                var currentPoints = mutablePrefs[PetDataStoreKeys.DECOR_POINTS] ?: 0
                currentPoints += 1
                mutablePrefs[PetDataStoreKeys.DECOR_POINTS] = currentPoints
                mutablePrefs[PetDataStoreKeys.LAST_DECOR_POINT_DATE] = today
            }

            val lastAffectionDate = mutablePrefs[PetDataStoreKeys.LAST_AFFECTION_UPDATE_DATE] ?: ""
            if (today != lastAffectionDate) {
                val currentAffection = mutablePrefs[PetDataStoreKeys.PET_AFFECTION_COUNT] ?: 0
                mutablePrefs[PetDataStoreKeys.PET_AFFECTION_COUNT] = currentAffection + 1
                mutablePrefs[PetDataStoreKeys.LAST_AFFECTION_UPDATE_DATE] = today
            }

            finalDecorPoints = mutablePrefs[PetDataStoreKeys.DECOR_POINTS] ?: 0
            mutablePrefs
        }
        return Pair(finalDecorPoints, didPointsIncrease)
    }

    // --- Composable 영역 ---
    @Composable
    fun mainAppScreen(
        petState: PetState,
        petType: PetType,
        decorPoints: Int,
    ) {
        var currentScreen by remember { mutableStateOf(MainScreen.PET_HOUSE) }
        val context = LocalContext.current

        Scaffold(
            bottomBar = {
                bottomNavigationBar(
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
                        petHouseScreen(
                            petState = petState,
                            petType = petType,
                            decorPoints = decorPoints,
                            onShowAd = { showAdAndBringPetBack(context) },
                        )

                    MainScreen.SETTINGS -> settingsScreen()
                }
            }
        }
    }

    @Composable
    fun bottomNavigationBar(
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

    // --- 펫 하우스 화면: 배경 이미지와 사랑주기 버튼, 꾸미기 포인트 ---
    @Composable
    fun petHouseScreen(
        petState: PetState,
        petType: PetType,
        decorPoints: Int,
        onShowAd: () -> Unit,
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val petIsRunaway = petState == PetState.RUNAWAY
        val petIsEgg = petState == PetState.EGG

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
                    painter = painterResource(id = PetVisualMapper.getRoomBackground(decorPoints)),
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
                            ).padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "✨ $decorPoints P",
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
                                    petType,
                                    petState,
                                ),
                        ),
                    contentDescription = "Pet",
                    modifier = Modifier.size(120.dp),
                )
            }

            // 2. 컨트롤러 (사랑 주기 / 가출 복귀 버튼)
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
                    Button(onClick = {
                        coroutineScope.launch {
                            val (totalPoints, didIncrease) = giveLoveAndGetPoints(context)

                            PetWidget().updateAll(context)

                            if (didIncrease) {
                                val message =
                                    when (totalPoints) {
                                        5 -> "방에 포근한 카펫이 생겼다! (5P 달성)"
                                        10 -> "따뜻한 벽난로가 생겼다! (10P 달성)"
                                        20 -> "폭신한 소파가 생겼다! (20P 달성)"
                                        else -> "사랑 주기 완료! (현재 $totalPoints P)"
                                    }
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "오늘은 이미 사랑을 줬어요. (총 $totalPoints P)",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                        }
                    }) {
                        Text("사랑 주기 ❤️ (포인트 +1)")
                    }
                }
            }
        }
    }

    // --- 설정 화면: 이름 짓기/변경 가능 ---
    @SuppressLint("FlowOperatorInvokedInComposition")
    @Composable
    fun settingsScreen() {
        var petNameInput by remember { mutableStateOf("") }
        var userNameInput by remember { mutableStateOf("") }

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        val currentPetName by context.dataStore.data
            .map {
                it[PetDataStoreKeys.PET_NAME] ?: "뽀짝이"
            }.collectAsState(initial = "뽀짝이")

        val currentUserName by context.dataStore.data
            .map {
                it[PetDataStoreKeys.USER_NAME] ?: "주인님"
            }.collectAsState(initial = "주인님")

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "현재 설정",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text("펫 이름: $currentPetName, 주인님 이름: $currentUserName")
            Spacer(modifier = Modifier.height(32.dp))

            // [A] 펫 이름 입력
            Text(
                text = "펫의 새 이름을 지어주세요!",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = petNameInput,
                onValueChange = { petNameInput = it },
                label = { Text("새 펫 이름 입력") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (petNameInput.isNotBlank()) {
                    coroutineScope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[PetDataStoreKeys.PET_NAME] = petNameInput
                        }
                        PetWidget().updateAll(context)
                        Toast.makeText(context, "펫 이름 저장 완료!", Toast.LENGTH_SHORT).show()
                        petNameInput = ""
                    }
                }
            }) {
                Text("펫 이름 저장하기")
            }

            Spacer(modifier = Modifier.height(48.dp))

            // [B] 유저 이름 입력
            Text(
                text = "주인님의 이름을 알려주세요!",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = userNameInput,
                onValueChange = { userNameInput = it },
                label = { Text("주인님 이름 입력") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (userNameInput.isNotBlank()) {
                    coroutineScope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[PetDataStoreKeys.USER_NAME] = userNameInput
                        }
                        PetWidget().updateAll(context)
                        Toast.makeText(context, "주인님 이름 저장 완료!", Toast.LENGTH_SHORT).show()
                        userNameInput = ""
                    }
                }
            }) {
                Text("주인님 이름 저장하기")
            }
        }
    }
}
