package com.starterkim.widgetbuddy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.glance.state.GlanceStateDefinition
import java.io.File

/**
 * 앱 전체에서 사용할 DataStore 인스턴스
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pet_prefs")

object PetStateDefinition : GlanceStateDefinition<Preferences> {
    override suspend fun getDataStore(
        context: Context,
        fileKey: String,
    ): DataStore<Preferences> = context.dataStore

    override fun getLocation(
        context: Context,
        fileKey: String,
    ): File = context.preferencesDataStoreFile("pet_prefs")
}

/**
 * DataStore의 Preference 키를 관리한다.
 */
object PetDataStoreKeys {
    // 펫 현재 상태
    val PET_STATE = stringPreferencesKey("pet_state")

    // 펫 종류
    val PET_TYPE = stringPreferencesKey("pet_type")

    // 펫 스탯
    val PET_SATIETY = intPreferencesKey("pet_satiety")
    val PET_JOY = intPreferencesKey("pet_joy")
    val PET_MISERY = intPreferencesKey("pet_misery")

    // 타임스탬프
    val LAST_UPDATED_TIMESTAMP = longPreferencesKey("last_updated_timestamp")
    val LAST_MAIN_APP_VISIT_TIMESTAMP = longPreferencesKey("last_main_app_visit_timestamp")
    val LAST_FED_TIMESTAMP = longPreferencesKey("last_fed_timestamp")
    val LAST_PLAYED_TIMESTAMP = longPreferencesKey("last_played_timestamp")
    val SATIETY_ZERO_TIMESTAMP = longPreferencesKey("satiety_zero_timestamp")
    val JOY_ZERO_TIMESTAMP = longPreferencesKey("joy_zero_timestamp")
    val LAST_AFFECTION_UPDATE_DATE = stringPreferencesKey("last_affection_update_date")
    val LAST_DECOR_POINT_DATE = stringPreferencesKey("last_decor_point_date")

    // 포인트 스탯
    val PET_AFFECTION_COUNT = intPreferencesKey("pet_affection_count")
    val DECOR_POINTS = intPreferencesKey("decor_points")

    // 이름 (펫 / 유저)
    val PET_NAME = stringPreferencesKey("pet_name")
    val USER_NAME = stringPreferencesKey("user_name")

    // 펫이 보내는 일회성 메시지
    val PET_MESSAGE = stringPreferencesKey("pet_message")
}
