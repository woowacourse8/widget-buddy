package com.starterkim.widgetbuddy.domain

import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * 펫의 상태를 계산하고 업데이트하는 순수 로직을 담당한다.
 * DataStore 의존성을 제거하고 PetStatus 도메인 모델을 사용한다.
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
    fun hatchPet(status: PetStatus): PetStatus {
        if (status.type != PetType.NONE) return status

        val availableTypes = PetType.entries.filter { it != PetType.NONE }
        val newPetType = availableTypes.random()

        val currentTime = System.currentTimeMillis()
        val today = LocalDate.now().toString()

        return status.copy(
            type = newPetType,
            state = PetState.IDLE,
            name = "뽀짝이",
            userName = "주인님",
            satiety = 100,
            joy = 100,
            misery = 0,
            affectionCount = 0,
            decorPoints = 0,
            lastUpdatedTimestamp = currentTime,
            lastMainAppVisitTimestamp = currentTime,
            lastFedTimestamp = currentTime,
            lastPlayedTimestamp = currentTime,
            satietyZeroTimestamp = 0L,
            joyZeroTimestamp = 0L,
            lastAffectionUpdateDate = "",
            message = ""
        )
    }

    // --- 상호작용 ---
    fun feedPet(status: PetStatus): PetStatus {
        val currentTime = System.currentTimeMillis()
        var updatedStatus = checkAndGrantDailyAffection(status)
        updatedStatus = updatedStatus.copy(lastUpdatedTimestamp = currentTime)

        val lastFedTime = if (updatedStatus.lastFedTimestamp == 0L)
            currentTime - SATIETY_TOTAL_DURATION_MS
        else updatedStatus.lastFedTimestamp

        if (currentTime - lastFedTime < SATIETY_FULL_DURATION_MS) {
            return updatedStatus.copy(message = "already_full")
        }

        return updatedStatus.copy(
            satiety = 100,
            lastFedTimestamp = currentTime,
            satietyZeroTimestamp = 0L,
            misery = (updatedStatus.misery - 10).coerceAtLeast(0),
            state = PetState.FULL_FEEDBACK,
            message = ""
        )
    }

    fun playWithPet(status: PetStatus): PetStatus {
        val currentTime = System.currentTimeMillis()
        var updatedStatus = checkAndGrantDailyAffection(status)
        updatedStatus = updatedStatus.copy(lastUpdatedTimestamp = currentTime)

        val lastPlayedTime = if (updatedStatus.lastPlayedTimestamp == 0L)
            currentTime - JOY_TOTAL_DURATION_MS
        else updatedStatus.lastPlayedTimestamp

        if (currentTime - lastPlayedTime < JOY_FULL_DURATION_MS) {
            return updatedStatus.copy(message = "already_playing")
        }

        return updatedStatus.copy(
            joy = 100,
            lastPlayedTimestamp = currentTime,
            joyZeroTimestamp = 0L,
            misery = (updatedStatus.misery - 10).coerceAtLeast(0),
            state = PetState.JOYFUL_FEEDBACK,
            message = ""
        )
    }

    fun restoreStateAfterFeedback(status: PetStatus): PetStatus {
        val currentTime = System.currentTimeMillis()

        val isLonely = (currentTime - status.lastMainAppVisitTimestamp) > ONE_DAY_MS
        val isWarning = status.misery >= 80
        val isSatietyLow = status.satiety <= 30
        val isBored = status.joy <= 30

        val nextState = when {
            status.misery >= 100 -> PetState.RUNAWAY
            isWarning -> PetState.WARNING
            isLonely -> PetState.NEEDS_LOVE
            isSatietyLow -> PetState.SATIETY_LOW
            isBored -> PetState.BORED
            else -> PetState.IDLE
        }

        return status.copy(state = nextState, message = "")
    }

    fun bringPetBack(status: PetStatus): PetStatus {
        val currentTime = System.currentTimeMillis()

        return status.copy(
            state = PetState.IDLE,
            satiety = 100,
            joy = 100,
            misery = 0,
            message = "",
            lastUpdatedTimestamp = currentTime,
            lastMainAppVisitTimestamp = currentTime,
            lastFedTimestamp = currentTime,
            lastPlayedTimestamp = currentTime,
            satietyZeroTimestamp = 0L,
            joyZeroTimestamp = 0L,
            lastAffectionUpdateDate = ""
        )
    }

    internal fun checkAndGrantDailyAffection(status: PetStatus): PetStatus {
        val today = LocalDate.now().toString()
        if (today != status.lastAffectionUpdateDate) {
            return status.copy(
                affectionCount = status.affectionCount + 1,
                lastAffectionUpdateDate = today
            )
        }
        return status
    }

    // -- 백그라운드 업데이트 함수 --
    fun applyPassiveUpdates(status: PetStatus): PetStatus {
        val currentTime = System.currentTimeMillis()
        val lastUpdated =
            if (status.lastUpdatedTimestamp == 0L) currentTime else status.lastUpdatedTimestamp
        val elapsedTimeMs = currentTime - lastUpdated

        if (elapsedTimeMs < TICK_INTERVAL_MS) {
            return status
        }

        val elapsedTicks = (elapsedTimeMs / TICK_INTERVAL_MS).toInt()

        val (newSatiety, satietyZeroTs) = calculateSatiety(status, currentTime)
        val (newJoy, joyZeroTs) = calculateJoy(status, currentTime)
        val newMisery = calculateMisery(status, newSatiety, newJoy, currentTime, elapsedTicks)

        var updatedStatus = status.copy(
            satiety = newSatiety,
            joy = newJoy,
            misery = newMisery,
            lastUpdatedTimestamp = currentTime,
            satietyZeroTimestamp = satietyZeroTs,
            joyZeroTimestamp = joyZeroTs,
            message = ""
        )

        if (updatedStatus.state == PetState.FULL_FEEDBACK || updatedStatus.state == PetState.JOYFUL_FEEDBACK) {
            return updatedStatus
        }

        if (updatedStatus.state != PetState.EGG) {
            val isLonely = (currentTime - updatedStatus.lastMainAppVisitTimestamp) > ONE_DAY_MS
            val hasRunAway = updatedStatus.misery >= 100
            val isWarning = updatedStatus.misery >= 80
            val isSatietyLow = updatedStatus.satiety <= 30
            val isBored = updatedStatus.joy <= 30

            val nextState = when {
                hasRunAway -> PetState.RUNAWAY
                isWarning -> PetState.WARNING
                isLonely -> PetState.NEEDS_LOVE
                isSatietyLow -> PetState.SATIETY_LOW
                isBored -> PetState.BORED
                else -> PetState.IDLE
            }
            updatedStatus = updatedStatus.copy(state = nextState)
        }

        return updatedStatus
    }

    private fun calculateSatiety(
        status: PetStatus,
        currentTime: Long,
    ): Pair<Int, Long> {
        val lastFedTime = if (status.lastFedTimestamp == 0L)
            currentTime - SATIETY_TOTAL_DURATION_MS
        else status.lastFedTimestamp

        val elapsedSinceFed = currentTime - lastFedTime
        val newSatiety = when {
            elapsedSinceFed < SATIETY_FULL_DURATION_MS -> 100
            elapsedSinceFed < SATIETY_TOTAL_DURATION_MS -> {
                val timeIntoDecay = elapsedSinceFed - SATIETY_FULL_DURATION_MS
                val decayProgress = timeIntoDecay.toDouble() / SATIETY_DECAY_DURATION_MS
                (100 * (1.0 - decayProgress)).toInt().coerceIn(0, 100)
            }

            else -> 0
        }

        var satietyZeroTime = status.satietyZeroTimestamp
        if (newSatiety <= 0 && satietyZeroTime == 0L) {
            satietyZeroTime = currentTime
        } else if (newSatiety > 0) {
            satietyZeroTime = 0L
        }

        return Pair(newSatiety, satietyZeroTime)
    }

    private fun calculateJoy(
        status: PetStatus,
        currentTime: Long,
    ): Pair<Int, Long> {
        val lastPlayedTime = if (status.lastPlayedTimestamp == 0L)
            currentTime - JOY_TOTAL_DURATION_MS
        else status.lastPlayedTimestamp

        val elapsedSincePlayed = currentTime - lastPlayedTime
        val newJoy = when {
            elapsedSincePlayed < JOY_FULL_DURATION_MS -> 100
            elapsedSincePlayed < JOY_TOTAL_DURATION_MS -> {
                val timeIntoDecay = elapsedSincePlayed - JOY_FULL_DURATION_MS
                val decayProcess = timeIntoDecay.toDouble() / JOY_DECAY_DURATION_MS
                (100 * (1.0 - decayProcess)).toInt().coerceIn(0, 100)
            }

            else -> 0
        }

        var joyZeroTime = status.joyZeroTimestamp
        if (newJoy <= 0 && joyZeroTime == 0L) {
            joyZeroTime = currentTime
        } else if (newJoy > 0) {
            joyZeroTime = 0L
        }

        return Pair(newJoy, joyZeroTime)
    }

    private fun calculateMisery(
        status: PetStatus,
        newSatiety: Int,
        newJoy: Int,
        currentTime: Long,
        elapsedTicks: Int,
    ): Int {
        var currentMisery = status.misery

        // 포만감 0 방치
        if (newSatiety <= 0) {
            val satietyZeroTime =
                if (status.satietyZeroTimestamp == 0L) currentTime else status.satietyZeroTimestamp
            if (currentTime - satietyZeroTime > MISERY_DELAY_MS) {
                currentMisery = (currentMisery + elapsedTicks * 5).coerceAtMost(100)
            }
        }

        // 즐거움 0 방치
        if (newJoy <= 0) {
            val joyZeroTime =
                if (status.joyZeroTimestamp == 0L) currentTime else status.joyZeroTimestamp
            if (currentTime - joyZeroTime > MISERY_DELAY_MS) {
                currentMisery = (currentMisery + elapsedTicks * 5).coerceAtMost(100)
            }
        }

        return currentMisery
    }
}
