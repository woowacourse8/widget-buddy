package com.starterkim.widgetbuddy.domain

/**
 * 펫의 전체 상태를 나타내는 순수 도메인 모델
 */
data class PetStatus(
    val type: PetType = PetType.NONE,
    val state: PetState = PetState.EGG,
    val name: String = "",
    val userName: String = "",
    val language: String = "ko",
    
    // 스탯
    val satiety: Int = 100,
    val joy: Int = 100,
    val misery: Int = 0,
    
    // 포인트 및 카운트
    val affectionCount: Int = 0,
    val decorPoints: Int = 0,
    
    // 타임스탬프 (ms)
    val lastUpdatedTimestamp: Long = 0L,
    val lastMainAppVisitTimestamp: Long = 0L,
    val lastFedTimestamp: Long = 0L,
    val lastPlayedTimestamp: Long = 0L,
    val lastTalkTimestamp: Long = 0L,
    val satietyZeroTimestamp: Long = 0L,
    val joyZeroTimestamp: Long = 0L,
    
    // 날짜 관련 (String 형식 유지하거나 LocalDate로 변환 가능)
    val lastAffectionUpdateDate: String = "",
    val lastDecorPointDate: String = "",
    
    // 피드백 메시지
    val message: String = ""
) {
    val isEgg: Boolean get() = type == PetType.NONE || state == PetState.EGG
    val hasRunAway: Boolean get() = state == PetState.RUNAWAY

    fun getDisplayName(context: android.content.Context): String {
        return name.ifBlank { context.getString(com.starterkim.widgetbuddy.R.string.default_pet_name) }
    }

    fun getDisplayUserName(context: android.content.Context): String {
        return userName.ifBlank { context.getString(com.starterkim.widgetbuddy.R.string.default_user_name) }
    }
}
