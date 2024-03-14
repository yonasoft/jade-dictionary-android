package com.yonasoft.jadedictionary.util

import com.yonasoft.jadedictionary.data.constants.StringType
fun determineStringType(input: String): StringType {
    val trimmedInput = input.trim()
    if (trimmedInput.isEmpty()) return StringType.English

    val hanziPattern = Regex("[\\u3400-\\u9FBF]")
    val pinyinPatternWithNumbers = Regex("[a-zA-ZüÜ]+[1-5]")
    val pinyinPatternWithTones = Regex("(\\b[a-zA-ZüÜ]*[āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜü]\\b)")

    return when {
        hanziPattern.containsMatchIn(trimmedInput) -> StringType.Hanzi
        pinyinPatternWithNumbers.containsMatchIn(trimmedInput) || pinyinPatternWithTones.containsMatchIn(trimmedInput) -> StringType.Pinyin
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
        val rearranged = syllable.replace(Regex("^([bpmfdtnlgkhjqxzcsryw]*)([aeiouüv]+)([ngh]?)([1-5]?)$"), "$1$2$3$4")
        // Move any tone numbers that appear between vowels and consonants to the end of the syllable
        rearranged.replace(Regex("([aeiouüv])([1-5])([ngh]?)"), "$1$3$2")
    }
    // Join the processed syllables back together with spaces, ensuring correct spacing between syllables
    return syllables.joinToString(" ").replace(Regex("([1-5])([bpmfdtnlgkhjqxzcsryw])"), "$1 $2")
}
fun normalizePinyinInput(input: String): String {
    var normalizedInput = input
    // Convert tone marks to numbers if present
    if (Regex("[āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜü]").containsMatchIn(input)) {
        normalizedInput = convertToneMarksToNumbers(normalizedInput)
    }
    // Rearrange tone numbers and add spaces as necessary
    normalizedInput = rearrangeToneNumbersAndAddSpaces(normalizedInput)
    return normalizedInput.trim()
}
