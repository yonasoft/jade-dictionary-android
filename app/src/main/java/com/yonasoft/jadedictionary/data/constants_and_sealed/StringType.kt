package com.yonasoft.jadedictionary.data.constants_and_sealed

sealed class StringType(val name:String) {
    data object Hanzi : StringType(name = "Hanzi")
    data object Pinyin : StringType(name = "Pinyin")
    data object English : StringType(name = "Definition")
}


