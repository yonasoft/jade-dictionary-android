package com.yonasoft.jadedictionary.data.constants_and_sealed

sealed class QuizType(val stringType1: StringType, val stringType2: StringType) {
    data object HanziDefinition :
        QuizType(stringType1 = StringType.Hanzi, stringType2= StringType.English)
    data object HanziPinyin :
        QuizType(stringType1 = StringType.Hanzi, stringType2 = StringType.Pinyin)
}