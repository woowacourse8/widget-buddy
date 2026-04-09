package com.starterkim.widgetbuddy.domain

import androidx.datastore.preferences.core.MutablePreferences
import com.starterkim.widgetbuddy.data.PetDataStoreKeys
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * 펫의 상태를 계산하고 업데이트하는 로직을 담당한다.
 */
object PetStateCalculator {
    // --- 시간 상수 ---
    private const val TICK_INTERVAL_MINUTES = 15L // (테스트용 15분 / 실제 1시간)
    private val TICK_INTERVAL_MS = TimeUnit.MINUTES.toMillis(TICK_INTERVAL_MINUTES)

    // 포만감
    private val SATIETY_FULL_DURATION_MS = TimeUnit.HOURS.toMillis(3)
    private val SATIETY_DECAY_DURATION_MS = TimeUnit.HOURS.toMillis(7)
    private val SATIETY_TOTAL_DURATION_MS = SATIETY_FULL_DURATION_MS + SATIETY_DECAY_DURATION_MS

    // 즐거움
    private val JOY_FULL_DURATION_MS = TimeUnit.HOURS.toMillis(2)
    private val JOY_DECAY_DURATION_MS = TimeUnit.HOURS.toMillis(5)
    private val JOY_TOTAL_DURATION_MS = JOY_FULL_DURATION_MS + JOY_DECAY_DURATION_MS

    // 불행(가출)
    private val MISERY_DELAY_MS = TimeUnit.HOURS.toMillis(12)
    private val ONE_DAY_MS = TimeUnit.DAYS.toMillis(1)

    // --- 초기화 ---
    fun hatchPet(prefs: MutablePreferences): MutablePreferences {
        val petTypeString = prefs[PetDataStoreKeys.PET_TYPE] ?: PetType.NONE.name
        val petType = PetType.fromString(petTypeString)
        if (petType != PetType.NONE) return prefs

        val availableTypes = PetType.entries.filter { it != PetType.NONE }
        val newPetType = availableTypes.random()
        initializeData(prefs, newPetType)
        return prefs
    }

    private fun initializeData(
        prefs: MutablePreferences,
        newPetType: PetType,
    ) {
        setDefaultStat(prefs)
        prefs[PetDataStoreKeys.PET_TYPE] = newPetType.name
        prefs[PetDataStoreKeys.PET_NAME] = "뽀짝이"
        prefs[PetDataStoreKeys.USER_NAME] = "주인님"
        prefs[PetDataStoreKeys.PET_AFFECTION_COUNT] = 0
        prefs[PetDataStoreKeys.DECOR_POINTS] = 0

        updateTimeNow(prefs)
        prefs[PetDataStoreKeys.LAST_AFFECTION_UPDATE_DATE] = ""
        prefs[PetDataStoreKeys.LAST_DECOR_POINT_DATE] = ""
    }

    private fun setDefaultStat(prefs: MutablePreferences) {
        prefs[PetDataStoreKeys.PET_STATE] = PetState.IDLE.name
        prefs[PetDataStoreKeys.PET_SATIETY] = 100
        prefs[PetDataStoreKeys.PET_JOY] = 100
        prefs[PetDataStoreKeys.PET_MISERY] = 0
        prefs[PetDataStoreKeys.PET_MESSAGE] = ""
    }

    private fun updateTimeNow(prefs: MutablePreferences) {
        prefs[PetDataStoreKeys.LAST_AFFECTION_UPDATE_DATE] = LocalDate.now().toString()

        val currentTime = System.currentTimeMillis()
        prefs[PetDataStoreKeys.LAST_UPDATED_TIMESTAMP] = currentTime
        prefs[PetDataStoreKeys.LAST_MAIN_APP_VISIT_TIMESTAMP] = currentTime
        prefs[PetDataStoreKeys.LAST_FED_TIMESTAMP] = currentTime
        prefs[PetDataStoreKeys.LAST_PLAYED_TIMESTAMP] = currentTime

        prefs[PetDataStoreKeys.SATIETY_ZERO_TIMESTAMP] = 0L
        prefs[PetDataStoreKeys.JOY_ZERO_TIMESTAMP] = 0L
    }

    // --- 상호작용 ---
    fun feedPet(prefs: MutablePreferences): MutablePreferences {
        val currentTime = System.currentTimeMillis()
        checkAndGrantDailyAffection(prefs)
        prefs[PetDataStoreKeys.LAST_UPDATED_TIMESTAMP] = System.currentTimeMillis()

        val lastFedTime = prefs[PetDataStoreKeys.LAST_FED_TIMESTAMP] ?: (currentTime - SATIETY_TOTAL_DURATION_MS)

        if (currentTime - lastFedTime < SATIETY_FULL_DURATION_MS) {
            prefs[PetDataStoreKeys.PET_MESSAGE] = "배불러요!"
            return prefs
        }

        // 스탯 업데이트
        prefs[PetDataStoreKeys.PET_SATIETY] = 100
        prefs[PetDataStoreKeys.LAST_FED_TIMESTAMP] = currentTime
        prefs[PetDataStoreKeys.SATIETY_ZERO_TIMESTAMP] = 0L
        prefs[PetDataStoreKeys.PET_MESSAGE] = ""

        val currentMisery = prefs[PetDataStoreKeys.PET_MISERY] ?: 0
        prefs[PetDataStoreKeys.PET_MISERY] = (currentMisery - 10).coerceAtLeast(0)

        // 피드백 상태
        prefs[PetDataStoreKeys.PET_STATE] = PetState.FULL_FEEDBACK.name

        return prefs
    }

    fun playWithPet(prefs: MutablePreferences): MutablePreferences {
        val currentTime = System.currentTimeMillis()
        checkAndGrantDailyAffection(prefs)
        prefs[PetDataStoreKeys.LAST_UPDATED_TIMESTAMP] = System.currentTimeMillis()
        val lastPlayedTime = prefs[PetDataStoreKeys.LAST_PLAYED_TIMESTAMP] ?: (currentTime - JOY_TOTAL_DURATION_MS)

        if (currentTime - lastPlayedTime < JOY_FULL_DURATION_MS) {
            prefs[PetDataStoreKeys.PET_MESSAGE] = "아직 안 심심해!"
            return prefs
        }

        // 스탯 업데이트
        prefs[PetDataStoreKeys.PET_JOY] = 100
        prefs[PetDataStoreKeys.LAST_PLAYED_TIMESTAMP] = currentTime
        prefs[PetDataStoreKeys.JOY_ZERO_TIMESTAMP] = 0L
        prefs[PetDataStoreKeys.PET_MESSAGE] = ""

        val currentMisery = prefs[PetDataStoreKeys.PET_MISERY] ?: 0
        prefs[PetDataStoreKeys.PET_MISERY] = (currentMisery - 10).coerceAtLeast(0)

        // 피드백 상태
        prefs[PetDataStoreKeys.PET_STATE] = PetState.JOYFUL_FEEDBACK.name

        return prefs
    }

    fun restoreStateAfterFeedback(prefs: MutablePreferences): MutablePreferences {
        val satiety = prefs[PetDataStoreKeys.PET_SATIETY] ?: 100
        val joy = prefs[PetDataStoreKeys.PET_JOY] ?: 100
        val misery = prefs[PetDataStoreKeys.PET_MISERY] ?: 0
        val lastAppVisitTime = prefs[PetDataStoreKeys.LAST_MAIN_APP_VISIT_TIMESTAMP] ?: System.currentTimeMillis()
        val currentTime = System.currentTimeMillis()

        val isLonely = (currentTime - lastAppVisitTime) > ONE_DAY_MS
        val isWarning = misery >= 80
        val isSatietyLow = satiety <= 30
        val isBored = joy <= 30

        val nextState =
            when {
                misery >= 100 -> PetState.RUNAWAY
                isWarning -> PetState.WARNING
                isLonely -> PetState.NEEDS_LOVE
                isSatietyLow -> PetState.SATIETY_LOW
                isBored -> PetState.BORED
                else -> PetState.IDLE
            }

        prefs[PetDataStoreKeys.PET_STATE] = nextState.name
        return prefs
    }

    fun bringPetBack(prefs: MutablePreferences): MutablePreferences {
        setDefaultStat(prefs)
        updateTimeNow(prefs)
        return prefs
    }

    internal fun checkAndGrantDailyAffection(prefs: MutablePreferences): MutablePreferences {
        val today = LocalDate.now().toString()
        val lastUpdateDate = prefs[PetDataStoreKeys.LAST_AFFECTION_UPDATE_DATE] ?: ""

        if (today != lastUpdateDate) {
            val currentAffection = prefs[PetDataStoreKeys.PET_AFFECTION_COUNT] ?: 0
            prefs[PetDataStoreKeys.PET_AFFECTION_COUNT] = currentAffection + 1
            prefs[PetDataStoreKeys.LAST_AFFECTION_UPDATE_DATE] = today
        }
        return prefs
    }

    // -- 백그라운드 업데이트 함수 --
    fun applyPassiveUpdates(prefs: MutablePreferences): MutablePreferences {
        val currentTime = System.currentTimeMillis()
        val elapsedTimeMs =
            currentTime - (prefs[PetDataStoreKeys.LAST_UPDATED_TIMESTAMP] ?: currentTime)

        if (elapsedTimeMs < TICK_INTERVAL_MS) {
            return prefs
        }

        val elapsedTicks = (elapsedTimeMs / TICK_INTERVAL_MS).toInt()

        val newSatiety = calculateSatiety(prefs, currentTime)
        val newJoy = calculateJoy(prefs, currentTime)
        val newMisery = calculateMisery(prefs, newSatiety, newJoy, currentTime, elapsedTicks)

        prefs[PetDataStoreKeys.PET_SATIETY] = newSatiety
        prefs[PetDataStoreKeys.PET_JOY] = newJoy
        prefs[PetDataStoreKeys.PET_MISERY] = newMisery
        prefs[PetDataStoreKeys.LAST_UPDATED_TIMESTAMP] = currentTime

        prefs[PetDataStoreKeys.PET_MESSAGE] = ""

        val currentState = PetState.fromString(prefs[PetDataStoreKeys.PET_STATE])

        if (currentState == PetState.FULL_FEEDBACK || currentState == PetState.JOYFUL_FEEDBACK) {
            return prefs
        }

        val lastAppVisitTime = prefs[PetDataStoreKeys.LAST_MAIN_APP_VISIT_TIMESTAMP] ?: currentTime
        val misery = prefs[PetDataStoreKeys.PET_MISERY] ?: 0

        val isLonely = (currentTime - lastAppVisitTime) > ONE_DAY_MS
        val hasRunAway = misery >= 100
        val isWarning = misery >= 80
        val isSatietyLow = newSatiety <= 30
        val isBored = newJoy <= 30

        if (PetState.fromString(prefs[PetDataStoreKeys.PET_STATE]) != PetState.EGG) {
            when {
                hasRunAway -> prefs[PetDataStoreKeys.PET_STATE] = PetState.RUNAWAY.name
                isWarning -> prefs[PetDataStoreKeys.PET_STATE] = PetState.WARNING.name
                isLonely -> prefs[PetDataStoreKeys.PET_STATE] = PetState.NEEDS_LOVE.name
                isSatietyLow -> prefs[PetDataStoreKeys.PET_STATE] = PetState.SATIETY_LOW.name
                isBored -> prefs[PetDataStoreKeys.PET_STATE] = PetState.BORED.name
                else -> prefs[PetDataStoreKeys.PET_STATE] = PetState.IDLE.name
            }
        }

        return prefs
    }

    private fun calculateSatiety(
        prefs: MutablePreferences,
        currentTime: Long,
    ): Int {
        val lastFedTime =
            prefs[PetDataStoreKeys.LAST_FED_TIMESTAMP] ?: (currentTime - SATIETY_TOTAL_DURATION_MS)
        val elapsedSinceFed = currentTime - lastFedTime
        val newSatiety =
            if (elapsedSinceFed < SATIETY_FULL_DURATION_MS) {
                100
            } else if (elapsedSinceFed < SATIETY_TOTAL_DURATION_MS) {
                val timeIntoDecay = elapsedSinceFed - SATIETY_FULL_DURATION_MS
                val decayProgress = timeIntoDecay.toDouble() / SATIETY_DECAY_DURATION_MS
                (100 * (1.0 - decayProgress)).toInt().coerceIn(0, 100)
            } else {
                0
            }
        return newSatiety
    }

    private fun calculateJoy(
        prefs: MutablePreferences,
        currentTime: Long,
    ): Int {
        val lastPlayedTime =
            prefs[PetDataStoreKeys.LAST_PLAYED_TIMESTAMP] ?: (currentTime - JOY_TOTAL_DURATION_MS)
        val elapsedSincePlayed = currentTime - lastPlayedTime
        val newJoy =
            if (elapsedSincePlayed < JOY_FULL_DURATION_MS) {
                100
            } else if (elapsedSincePlayed < JOY_TOTAL_DURATION_MS) {
                val timeIntoDecay = elapsedSincePlayed - JOY_FULL_DURATION_MS
                val decayProcess = timeIntoDecay.toDouble() / JOY_DECAY_DURATION_MS
                (100 * (1.0 - decayProcess)).toInt().coerceIn(0, 100)
            } else {
                0
            }
        return newJoy
    }

    private fun calculateMisery(
        prefs: MutablePreferences,
        newSatiety: Int,
        newJoy: Int,
        currentTime: Long,
        elapsedTicks: Int,
    ): Int {
        var currentMisery = prefs[PetDataStoreKeys.PET_MISERY] ?: 0

        // 포만감 0 방치
        if (newSatiety <= 0) {
            var satietyZeroTime = prefs[PetDataStoreKeys.SATIETY_ZERO_TIMESTAMP] ?: 0L
            if (satietyZeroTime == 0L) {
                satietyZeroTime = currentTime
                prefs[PetDataStoreKeys.SATIETY_ZERO_TIMESTAMP] = currentTime
            }
            if (currentTime - satietyZeroTime > MISERY_DELAY_MS) {
                currentMisery = (currentMisery + elapsedTicks * 5).coerceAtMost(100)
            }
        }

        // 즐거움 0 방치
        if (newJoy <= 0) {
            var joyZeroTime = prefs[PetDataStoreKeys.JOY_ZERO_TIMESTAMP] ?: 0L
            if (joyZeroTime == 0L) {
                joyZeroTime = currentTime
                prefs[PetDataStoreKeys.JOY_ZERO_TIMESTAMP] = currentTime
            }
            if (currentTime - joyZeroTime > MISERY_DELAY_MS) {
                currentMisery = (currentMisery + elapsedTicks * 5).coerceAtMost(100)
            }
        }

        return currentMisery
    }
}
