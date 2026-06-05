package com.starterkim.widgetbuddy.data

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.glance.state.GlanceStateDefinition
import java.io.File
import java.util.Locale

/**
 * 앱 전체에서 사용할 DataStore 인스턴스
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pet_prefs")

/**
 * PetRepository 인스턴스를 제공하는 확장 프로퍼티
 */
val Context.petRepository: PetRepository
    get() = PetRepository(this)

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
    
    // 언어 설정 (ko, en)
    val LANGUAGE = stringPreferencesKey("language")

    // 펫이 보내는 일회성 메시지
    val PET_MESSAGE = stringPreferencesKey("pet_message")
}

/**
 * 지정된 언어 태그가 적용된 Context를 반환한다.
 * 위젯이 시스템 locale이 아닌 사용자가 선택한 언어로 문자열을 해석할 때 사용한다.
 */
fun Context.localizedFor(languageTag: String): Context {
    val locale = Locale.forLanguageTag(languageTag)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

/**
 * Preferences 객체를 PetStatus 도메인 모델로 변환한다.
 */
fun Preferences.toPetStatus(context: android.content.Context): com.starterkim.widgetbuddy.domain.PetStatus {
    return com.starterkim.widgetbuddy.domain.PetStatus(
        type = com.starterkim.widgetbuddy.domain.PetType.fromId(this[PetDataStoreKeys.PET_TYPE]),
        state = com.starterkim.widgetbuddy.domain.PetState.fromId(this[PetDataStoreKeys.PET_STATE]),
        name = this[PetDataStoreKeys.PET_NAME] ?: "",
        userName = this[PetDataStoreKeys.USER_NAME] ?: "",
        language = this[PetDataStoreKeys.LANGUAGE] ?: "ko",
        satiety = this[PetDataStoreKeys.PET_SATIETY] ?: 100,
        joy = this[PetDataStoreKeys.PET_JOY] ?: 100,
        misery = this[PetDataStoreKeys.PET_MISERY] ?: 0,
        affectionCount = this[PetDataStoreKeys.PET_AFFECTION_COUNT] ?: 0,
        decorPoints = this[PetDataStoreKeys.DECOR_POINTS] ?: 0,
        lastUpdatedTimestamp = this[PetDataStoreKeys.LAST_UPDATED_TIMESTAMP] ?: 0L,
        lastMainAppVisitTimestamp = this[PetDataStoreKeys.LAST_MAIN_APP_VISIT_TIMESTAMP] ?: 0L,
        lastFedTimestamp = this[PetDataStoreKeys.LAST_FED_TIMESTAMP] ?: 0L,
        lastPlayedTimestamp = this[PetDataStoreKeys.LAST_PLAYED_TIMESTAMP] ?: 0L,
        satietyZeroTimestamp = this[PetDataStoreKeys.SATIETY_ZERO_TIMESTAMP] ?: 0L,
        joyZeroTimestamp = this[PetDataStoreKeys.JOY_ZERO_TIMESTAMP] ?: 0L,
        lastAffectionUpdateDate = this[PetDataStoreKeys.LAST_AFFECTION_UPDATE_DATE] ?: "",
        lastDecorPointDate = this[PetDataStoreKeys.LAST_DECOR_POINT_DATE] ?: "",
        message = this[PetDataStoreKeys.PET_MESSAGE] ?: ""
    )
}
