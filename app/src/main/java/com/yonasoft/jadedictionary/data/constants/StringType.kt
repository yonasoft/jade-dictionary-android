package com.yonasoft.jadedictionary.data.constants

sealed class StringType {
    data object Hanzi : StringType()
    data object Pinyin : StringType()
    data object English : StringType()
}


