package com.starterkim.widgetbuddy.data

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import com.starterkim.widgetbuddy.domain.PetStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * PetStatus 도메인 모델과 DataStore 간의 데이터를 중계한다.
 */
class PetRepository(private val context: Context) {

    /**
     * 현재 펫의 상태를 Flow로 반환한다.
     */
    val petStatus: Flow<PetStatus> = context.dataStore.data.map { it.toPetStatus(context) }

    /**
     * 펫 상태를 업데이트한다. (고차 함수를 통해 도메인 로직 적용 가능)
     */
    suspend fun updateStatus(transform: (PetStatus) -> PetStatus) {
        context.dataStore.edit { prefs ->
            val currentStatus = prefs.toPetStatus(context)
            val newStatus = transform(currentStatus)
            saveToPrefs(newStatus, prefs)
        }
    }

    private fun saveToPrefs(status: PetStatus, prefs: MutablePreferences) {
        prefs[PetDataStoreKeys.PET_TYPE] = status.type.id
        prefs[PetDataStoreKeys.PET_STATE] = status.state.id
        prefs[PetDataStoreKeys.PET_NAME] = status.name
        prefs[PetDataStoreKeys.USER_NAME] = status.userName
        prefs[PetDataStoreKeys.LANGUAGE] = status.language
        prefs[PetDataStoreKeys.PET_SATIETY] = status.satiety
        prefs[PetDataStoreKeys.PET_JOY] = status.joy
        prefs[PetDataStoreKeys.PET_MISERY] = status.misery
        prefs[PetDataStoreKeys.PET_AFFECTION_COUNT] = status.affectionCount
        prefs[PetDataStoreKeys.DECOR_POINTS] = status.decorPoints
        prefs[PetDataStoreKeys.LAST_UPDATED_TIMESTAMP] = status.lastUpdatedTimestamp
        prefs[PetDataStoreKeys.LAST_MAIN_APP_VISIT_TIMESTAMP] = status.lastMainAppVisitTimestamp
        prefs[PetDataStoreKeys.LAST_FED_TIMESTAMP] = status.lastFedTimestamp
        prefs[PetDataStoreKeys.LAST_PLAYED_TIMESTAMP] = status.lastPlayedTimestamp
        prefs[PetDataStoreKeys.LAST_TALK_TIMESTAMP] = status.lastTalkTimestamp
        prefs[PetDataStoreKeys.SATIETY_ZERO_TIMESTAMP] = status.satietyZeroTimestamp
        prefs[PetDataStoreKeys.JOY_ZERO_TIMESTAMP] = status.joyZeroTimestamp
        prefs[PetDataStoreKeys.LAST_AFFECTION_UPDATE_DATE] = status.lastAffectionUpdateDate
        prefs[PetDataStoreKeys.LAST_DECOR_POINT_DATE] = status.lastDecorPointDate
        prefs[PetDataStoreKeys.PET_MESSAGE] = status.message
    }
}
