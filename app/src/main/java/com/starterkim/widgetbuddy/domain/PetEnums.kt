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
...
    DRAGON(
        id = "dragon",
        idleImage = R.drawable.dragon_idle,
...
    NONE(
        id = "none",
        idleImage = R.drawable.egg,
...
    ;

    companion object {
        fun fromId(id: String?): PetType = entries.find { it.id == id || it.name == id } ?: NONE
    }
}
