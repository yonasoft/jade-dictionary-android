package com.yonasoft.jadedictionary.data.constants

import com.yonasoft.jadedictionary.R

sealed class PracticeMode(val title: String, val description: String, val icon: Int) {
    companion object{
        val modes= listOf(FlashCards, MultipleChoice)
    }
    data object FlashCards :
        PracticeMode(title = "Flash Card", description = "Practice with interactive flash cards.", icon = R.drawable.baseline_playing_cards_24)
    data object MultipleChoice :
        PracticeMode(title = "Multiple Choice ", description = "Test your knowledge with multiple choice questions.", icon = R.drawable.baseline_checklist_24)
}