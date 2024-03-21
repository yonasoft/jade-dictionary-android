package com.yonasoft.jadedictionary.data.constants

import com.yonasoft.jadedictionary.R

sealed class PracticeMode(val title: String, val description: String, val icon: Int) {
    data object FlashCards :
        PracticeMode(title = "Flash Card", description = "Practice with interactive flash cards.", icon = R.drawable.baseline_playing_cards_24)
    data object MultipleChoice :
        PracticeMode(title = "Multiple Choice ", description = "Test your knowledge with multiple choice questions.", icon = R.drawable.baseline_checklist_24)

    val modes= listOf(PracticeMode.FlashCards, PracticeMode.MultipleChoice)
}