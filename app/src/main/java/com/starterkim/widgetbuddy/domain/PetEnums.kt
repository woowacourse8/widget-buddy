package com.starterkim.widgetbuddy.domain

import androidx.annotation.Keep
import com.starterkim.widgetbuddy.R

/**
 * 펫의 상태를 나타내는 Enum
 */
@Keep
enum class PetState(val id: String) {
    EGG("egg"),
    IDLE("idle"),
    NEEDS_LOVE("needs_love"),
    SATIETY_LOW("satiety_low"),
    BORED("bored"),
    WARNING("warning"),
    RUNAWAY("runaway"),
    FULL_FEEDBACK("full_feedback"),
    JOYFUL_FEEDBACK("joyful_feedback"),
    ;

    companion object {
        fun fromId(id: String?): PetState = entries.find { it.id == id || it.name == id } ?: EGG
    }
}

/**
 * 펫의 종류를 나타내는 Enum
 */
@Keep
enum class PetType(
    val id: String,
    val idleImage: Int,
    val lonelyImage: Int,
    val boredImage: Int,
    val hungryImage: Int,
    val fullImage: Int,
    val joyfulImage: Int,
    val warningImage: Int,
) {
    BAPSAE(
        id = "bapsae",
        idleImage = R.drawable.bapsae_idle,
        lonelyImage = R.drawable.bapsae_lonely,
        boredImage = R.drawable.bapsae_bored,
        hungryImage = R.drawable.bapsae_hungry,
        fullImage = R.drawable.bapsae_full,
        joyfulImage = R.drawable.bapsae_joyful,
        warningImage = R.drawable.bapsae_warning,
    ),
    DRAGON(
        id = "dragon",
        idleImage = R.drawable.dragon_idle,
        lonelyImage = R.drawable.dragon_lonely,
        boredImage = R.drawable.dragon_bored,
        hungryImage = R.drawable.dragon_hungry,
        fullImage = R.drawable.dragon_full,
        joyfulImage = R.drawable.dragon_joyful,
        warningImage = R.drawable.dragon_warning,
    ),
    NONE(
        id = "none",
        idleImage = R.drawable.egg,
        lonelyImage = R.drawable.egg,
        boredImage = R.drawable.egg,
        hungryImage = R.drawable.egg,
        fullImage = R.drawable.egg,
        joyfulImage = R.drawable.egg,
        warningImage = R.drawable.egg,
    ),
    ;

    companion object {
        fun fromId(id: String?): PetType = entries.find { it.id == id || it.name == id } ?: NONE
    }
}
