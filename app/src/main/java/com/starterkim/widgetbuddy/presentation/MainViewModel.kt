package com.starterkim.widgetbuddy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starterkim.widgetbuddy.data.PetRepository
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.domain.PetStateCalculator
import com.starterkim.widgetbuddy.domain.PetStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PetRepository) : ViewModel() {

    val petStatus: StateFlow<PetStatus> = repository.petStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PetStatus()
        )

    fun hatchPet() {
        viewModelScope.launch {
            repository.updateStatus { PetStateCalculator.hatchPet(it) }
        }
    }

    fun feedPet() {
        viewModelScope.launch {
            repository.updateStatus { PetStateCalculator.feedPet(it) }
        }
    }

    fun playWithPet() {
        viewModelScope.launch {
            repository.updateStatus { PetStateCalculator.playWithPet(it) }
        }
    }

    fun restoreState() {
        viewModelScope.launch {
            repository.updateStatus { PetStateCalculator.restoreStateAfterFeedback(it) }
        }
    }

    fun bringPetBack() {
        viewModelScope.launch {
            repository.updateStatus { PetStateCalculator.bringPetBack(it) }
        }
    }

    fun updateMainAppVisit() {
        viewModelScope.launch {
            repository.updateStatus { it.copy(lastMainAppVisitTimestamp = System.currentTimeMillis()) }
        }
    }

    /**
     * 사랑을 주고 포인트를 획득하는 로직.
     * 결과 메시지를 콜백으로 전달한다.
     */
    /**
     * 사랑을 주고 포인트를 획득하는 로직.
     * 새로 획득한 포인트 합계를 콜백으로 전달하고, 오늘 이미 줬다면 null을 전달한다.
     */
    fun giveLoveAndGetPoints(onResult: (newPoints: Int?) -> Unit) {
        viewModelScope.launch {
            var earnedPoints: Int? = null
            repository.updateStatus { status ->
                if (status.isEgg || status.hasRunAway) {
                    return@updateStatus status
                }

                val today = java.time.LocalDate.now().toString()
                var updatedStatus = status.copy(
                    lastMainAppVisitTimestamp = System.currentTimeMillis(),
                    state = PetState.IDLE
                )

                // 포인트 지급 로직
                if (today != status.lastDecorPointDate) {
                    val newPoints = status.decorPoints + 1
                    updatedStatus = updatedStatus.copy(
                        decorPoints = newPoints,
                        lastDecorPointDate = today
                    )
                    earnedPoints = newPoints
                }

                // 일일 애정 지수 지급 로직
                if (today != status.lastAffectionUpdateDate) {
                    updatedStatus = updatedStatus.copy(
                        affectionCount = status.affectionCount + 1,
                        lastAffectionUpdateDate = today
                    )
                }

                updatedStatus
            }
            onResult(earnedPoints)
        }
    }

    fun updatePetName(name: String) {
        viewModelScope.launch {
            repository.updateStatus { it.copy(name = name) }
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            repository.updateStatus { it.copy(userName = name) }
        }
    }

    fun updateLanguage(langCode: String) {
        viewModelScope.launch {
            repository.updateStatus { it.copy(language = langCode) }
        }
    }
}
