package com.mohammad.kk.mycalculator.utils

import java.util.regex.Pattern

internal const val circle_1 = "➀"
internal const val circle_2 = "➁"
internal const val circle_3 = "➂"
internal const val circle_4 = "➃"
internal const val circle_5 = "➄"
internal const val circle_6 = "➅"
internal const val circle_7 = "➆"
internal const val circle_8 = "➇"
internal const val circle_9 = "➈"

internal fun getDecimalFormattedString(value: String): String {
    return if (value.contains(".")) {
        val x = value.split("\\.".toRegex()).toTypedArray()
        x[0].replace("(\\d)(?=(\\d{3})+$)".toRegex(), "$1,") + "." + x[1]
    } else {
        value.replace("(\\d)(?=(\\d{3})+$)".toRegex(), "$1,")
    }
}
internal fun onlyNumber(value: String):String{
    var text = value
    val p = Pattern.compile("[0-9]*\\.?[0-9]*")
    val m = p.matcher(text)
    while (m.find()){
        text = text.replaceFirst(m.group(), getDecimalFormattedString(m.group()))
    }
    return text
}