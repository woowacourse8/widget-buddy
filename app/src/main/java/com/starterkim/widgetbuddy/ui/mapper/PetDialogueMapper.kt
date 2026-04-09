package com.starterkim.widgetbuddy.ui.mapper

import com.starterkim.widgetbuddy.domain.PetState

object PetDialogueMapper {
    fun getDialogue(
        state: PetState,
        satiety: Int,
        joy: Int,
        petName: String,
        userName: String,
        petMessage: String,
    ): String {
        if (petMessage.isNotBlank()) return petMessage

        return when (state) {
            PetState.EGG -> "..."
            PetState.IDLE -> getIdleDialogues(petName, satiety, joy, userName).random()
            PetState.NEEDS_LOVE -> getNeedsLoveDialogue().random()

            // 스탯 부족 상태
            PetState.SATIETY_LOW -> "배고파... 밥 줘! (포만감: $satiety)"
            PetState.BORED -> "심심해...놀아줘! (즐거움: $joy)"

            PetState.FULL_FEEDBACK -> getFullFeedbackDialogue(satiety).random()
            PetState.JOYFUL_FEEDBACK -> getJoyfulFeedbackDialogue(joy).random()

            // 경고 및 가출 상태
            PetState.WARNING -> getWarningDialogue().random()
            PetState.RUNAWAY -> getRunAwayDialogue().random()
        }
    }

    private fun getIdleDialogues(
        petName: String,
        satiety: Int,
        joy: Int,
        userName: String,
    ): List<String> {
        // 기존 코드 유지
        val statsText = "포만감: $satiety, 즐거움: $joy"

        return listOf(
            statsText,
            "$userName 보고싶어!",
            "오늘 날씨 어때~",
            "헤헤...",
            "지금 몇 시지?",
            "뒹굴뒹굴...",
            "보고 있었어?",
            "$userName 뭐해?",
            "나 잘 지내고 있어!",
            "역시 집이 최고야.",
            "$petName 이뻐?",
        )
    }

    private fun getFullFeedbackDialogue(satiety: Int): List<String> =
        listOf(
            "냠냠! 정말 맛있었어! 😋",
            "배가 빵빵해~ 최고야! (포만감: $satiety)",
            "든든해졌어! 고마워!",
            "잠깐 졸린다.. Zzz",
        )

    private fun getJoyfulFeedbackDialogue(joy: Int): List<String> =
        listOf(
            "까르르! 너무 신나! >_<",
            "세상에서 내가 제일 행복해! (즐거움: $joy)",
            "에너지가 가득 찼어! 고마워!",
            "다음에 또 놀자!",
        )

    private fun getNeedsLoveDialogue(): List<String> =
        listOf(
            "우리 집에 언제 올 거야?",
            "앱에서 나 좀 만나줘~",
            "할 말 있는데..(톡톡)",
            "(두리번)혹시 지금 시간 돼?",
        )

    private fun getWarningDialogue(): List<String> =
        listOf(
            "이젠..정말 지쳤어.",
            "아무래도.. 우리 인연이 아닌가 봐",
            "나..혹시..버려진 거야? ㅠㅠ",
            "나.. 완전히 잊힌 거 같아.",
            "떠날 준비를.. 해야 할지도 모르겠어.",
        )

    private fun getRunAwayDialogue(): List<String> =
        listOf(
            "잠시.. 멀리 여행 좀 다녀올게.",
            "기다렸는데.. 결국 오지 않았네. \n 잘 지내.",
            "더이상 여기 있을 이유가 없어진 것 같아. \n 안녕.",
            "나 혹시 귀찮아졌어..? \n 조용히 사라져줄게.",
        )
}
