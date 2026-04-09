package com.starterkim.widgetbuddy.ui.mapper

import com.starterkim.widgetbuddy.R
import com.starterkim.widgetbuddy.domain.PetState
import com.starterkim.widgetbuddy.domain.PetType

/**
 * 펫의 상태(type, state)를 기반으로
 * 적절한 시각적 리소스(이미지) ID를 반환(매핑)합니다.
 */
object PetVisualMapper {
    fun getImageResource(
        type: PetType,
        state: PetState,
    ): Int {
        if (state == PetState.EGG) {
            R.drawable.egg
        }

        return when (state) {
            PetState.IDLE -> type.idleImage
            PetState.WARNING -> type.warningImage
            PetState.RUNAWAY -> R.drawable.message

            PetState.NEEDS_LOVE -> type.lonelyImage
            PetState.BORED -> type.boredImage
            PetState.SATIETY_LOW -> type.hungryImage

            PetState.FULL_FEEDBACK -> type.fullImage
            PetState.JOYFUL_FEEDBACK -> type.joyfulImage

            else -> type.idleImage
        }
    }

    fun getRoomBackground(decorPoints: Int): Int =
        when {
            decorPoints >= 20 -> R.drawable.background_3
            decorPoints >= 10 -> R.drawable.background_2
            decorPoints >= 5 -> R.drawable.background_1
            else -> R.drawable.background_0
        }
}
