package com.yonasoft.jadedictionary.util

import com.yonasoft.jadedictionary.data.constants.StringType
import com.yonasoft.jadedictionary.data.models.Word

fun determineStringType(input: String): StringType {
    val trimmedInput = input.trim()
    if (trimmedInput.isEmpty()) return StringType.English

    val hanziPattern = Regex("[\\u3400-\\u9FBF]")
    val pinyinPatternWithNumbers = Regex("[a-zA-ZüÜ]+[1-5]")
    val pinyinPatternWithTones = Regex("(\\b[a-zA-ZüÜ]*[āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜü]\\b)")

    return when {
        hanziPattern.containsMatchIn(trimmedInput) -> StringType.Hanzi
        pinyinPatternWithNumbers.containsMatchIn(trimmedInput) || pinyinPatternWithTones.containsMatchIn(
            trimmedInput
        ) -> StringType.Pinyin

        else -> StringType.English
    }
}

fun convertToneMarksToNumbers(input: String): String {
    val toneMarksToNumbers = mapOf(
        'ā' to "a1", 'á' to "a2", 'ǎ' to "a3", 'à' to "a4",
        'ē' to "e1", 'é' to "e2", 'ě' to "e3", 'è' to "e4",
        'ī' to "i1", 'í' to "i2", 'ǐ' to "i3", 'ì' to "i4",
        'ō' to "o1", 'ó' to "o2", 'ǒ' to "o3", 'ò' to "o4",
        'ū' to "u1", 'ú' to "u2", 'ǔ' to "u3", 'ù' to "u4",
        'ǖ' to "ü1", 'ǘ' to "ü2", 'ǚ' to "ü3", 'ǜ' to "ü4",
        // Map for 'ü' without tone to v for consistency
        'ü' to "v"
    )
    // Replace each character in the input with its mapping in toneMarksToNumbers
    return input.map { char -> toneMarksToNumbers[char] ?: char.toString() }.joinToString("")
}

fun rearrangeToneNumbersAndAddSpaces(input: String): String {
    // Split the input by whitespace, then process each syllable
    val syllables = input.split("\\s+".toRegex()).map { syllable ->
        // First, rearrange the syllable to ensure tones are at the end
        val rearranged = syllable.replace(
            Regex("^([bpmfdtnlgkhjqxzcsryw]*)([aeiouüv]+)([ngh]?)([1-5]?)$"),
            "$1$2$3$4"
        )
        // Move any tone numbers that appear between vowels and consonants to the end of the syllable
        rearranged.replace(Regex("([aeiouüv])([1-5])([ngh]?)"), "$1$3$2")
    }
    // Join the processed syllables back together with spaces, ensuring correct spacing between syllables
    return syllables.joinToString(" ").replace(Regex("([1-5])([bpmfdtnlgkhjqxzcsryw])"), "$1 $2")
}

fun normalizePinyinInput(input: String): String {
    var normalizedInput = input
    if (Regex("[āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜü]").containsMatchIn(input)) {
        normalizedInput = convertToneMarksToNumbers(normalizedInput)
    }
    normalizedInput = rearrangeToneNumbersAndAddSpaces(normalizedInput)
    return normalizedInput.trim()
}

fun extractStringFromWord(word: Word, stringType: StringType): String {
    val hanzi = word.simplified + if (word.traditional != null) "(${word.traditional})" else ""
    return when (stringType) {
        StringType.English -> word.definition?:""
        StringType.Pinyin -> convertNumberedPinyinToAccented(word.pinyin?:"")
        StringType.Hanzi -> hanzi
    }
}

fun convertNumberedPinyinToAccented(pinyin: String): String {
    val vowels = "aeiouüv"
    val toneMarks = mapOf(
        'a' to arrayOf("a", "ā", "á", "ǎ", "à"),
        'e' to arrayOf("e", "ē", "é", "ě", "è"),
        'i' to arrayOf("i", "ī", "í", "ǐ", "ì"),
        'o' to arrayOf("o", "ō", "ó", "ǒ", "ò"),
        'u' to arrayOf("u", "ū", "ú", "ǔ", "ù"),
        'ü' to arrayOf("ü", "ǖ", "ǘ", "ǚ", "ǜ"),
        'v' to arrayOf("ü", "ǖ", "ǘ", "ǚ", "ǜ") // 'v' is used for 'ü' in some systems
    )

    // Determine the primary vowel in a syllable for tone marking
    fun primaryVowel(syllable: String): Char {
        val order = "aoeüiu"
        for (vowel in order) {
            if (vowel in syllable) return vowel
        }
        return ' ' // Return space if no vowel is found, which should not happen
    }

    // Convert a single syllable from numbered to accented form
    fun convertSyllable(syllable: String): String {
        val toneNumber = syllable.takeLast(1).toString().toIntOrNull()
        if (toneNumber == null || toneNumber !in 1..4) return syllable // Return original if no valid tone

        val baseSyllable = syllable.dropLast(1)
        val vowelToMark = primaryVowel(baseSyllable)

        // Replace the primary vowel with its accented version
        var marked = false
        val convertedSyllable = baseSyllable.map { char ->
            if (!marked && char == vowelToMark) {
                marked = true
                toneMarks[char]?.get(toneNumber) ?: char.toString()
            } else char.toString()
        }.joinToString("")

        return convertedSyllable
    }

    // Process each syllable in the input string
    return pinyin.split(" ").joinToString(" ") { syllable ->
        convertSyllable(syllable)
    }
}



