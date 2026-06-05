package com.starterkim.widgetbuddy.presentation.mapper

import android.content.Context
import com.starterkim.widgetbuddy.R
import com.starterkim.widgetbuddy.domain.PetState

object PetDialogueMapper {
    fun getDialogue(
        context: Context,
        state: PetState,
        satiety: Int,
        joy: Int,
        petName: String,
        userName: String,
        petMessage: String,
    ): String {
        if (petMessage.isNotBlank()) {
            return when (petMessage) {
                "already_full" -> context.getString(R.string.already_full)
                "already_playing" -> context.getString(R.string.already_playing)
                else -> petMessage
            }
        }

        return when (state) {
            PetState.EGG -> "..."
            PetState.IDLE -> getIdleDialogues(context, petName, satiety, joy, userName).random()
            PetState.NEEDS_LOVE -> getNeedsLoveDialogue(context).random()

            PetState.SATIETY_LOW -> context.getString(R.string.state_satiety_low, satiety)
            PetState.BORED -> context.getString(R.string.state_bored, joy)

            PetState.FULL_FEEDBACK -> getFullFeedbackDialogue(context, satiety).random()
            PetState.JOYFUL_FEEDBACK -> getJoyfulFeedbackDialogue(context, joy).random()

            PetState.WARNING -> getWarningDialogue(context).random()
            PetState.RUNAWAY -> getRunAwayDialogue(context).random()
        }
    }

    private fun getIdleDialogues(
        context: Context,
        petName: String,
        satiety: Int,
        joy: Int,
        userName: String,
    ): List<String> {
        return listOf(
            context.getString(R.string.stats_format, satiety, joy),
            context.getString(R.string.dialogue_miss_user, userName),
            context.getString(R.string.dialogue_weather),
            context.getString(R.string.dialogue_hehe),
            context.getString(R.string.dialogue_what_time),
            context.getString(R.string.dialogue_rolling),
            context.getString(R.string.dialogue_watching),
            context.getString(R.string.dialogue_what_doing, userName),
            context.getString(R.string.dialogue_doing_well),
            context.getString(R.string.dialogue_home_best),
            context.getString(R.string.dialogue_pet_cute, petName),
        )
    }

    private fun getFullFeedbackDialogue(context: Context, satiety: Int): List<String> =
        listOf(
            context.getString(R.string.feedback_yam),
            context.getString(R.string.feedback_full, satiety),
            context.getString(R.string.feedback_full_thanks),
            context.getString(R.string.feedback_sleepy),
        )

    private fun getJoyfulFeedbackDialogue(context: Context, joy: Int): List<String> =
        listOf(
            context.getString(R.string.feedback_joyful),
            context.getString(R.string.feedback_happy, joy),
            context.getString(R.string.feedback_energy),
            context.getString(R.string.feedback_play_again),
        )

    private fun getNeedsLoveDialogue(context: Context): List<String> =
        listOf(
            context.getString(R.string.needs_love_1),
            context.getString(R.string.needs_love_2),
            context.getString(R.string.needs_love_3),
            context.getString(R.string.needs_love_4),
        )

    private fun getWarningDialogue(context: Context): List<String> =
        listOf(
            context.getString(R.string.warning_1),
            context.getString(R.string.warning_2),
            context.getString(R.string.warning_3),
            context.getString(R.string.warning_4),
            context.getString(R.string.warning_5),
        )

    private fun getRunAwayDialogue(context: Context): List<String> =
        listOf(
            context.getString(R.string.runaway_1),
            context.getString(R.string.runaway_2),
            context.getString(R.string.runaway_3),
            context.getString(R.string.runaway_4),
        )
}
