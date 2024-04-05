package com.yonasoft.jadedictionary.data.constants_and_sealed

import java.util.Locale

enum class SortOption {
    TITLE_ASC, TITLE_DESC, DATE_RECENT, DATE_LEAST_RECENT;

    override fun toString(): String {
        return name.replace('_', ' ')
            .lowercase(Locale.getDefault())
            .replaceFirstChar { it.uppercase() }
    }
}
