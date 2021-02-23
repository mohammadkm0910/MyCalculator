package com.mohammad.kk.mycalculator.utils

import java.util.regex.Pattern

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
internal fun String.toEnDigits():String {
    var getStr = this
    val digits = arrayOf(
        arrayOf("۰", "0"),
        arrayOf("۱", "1"),
        arrayOf("۲", "2"),
        arrayOf("۳", "3"),
        arrayOf("۴", "4"),
        arrayOf("۵", "5"),
        arrayOf("۶", "6"),
        arrayOf("۷", "7"),
        arrayOf("۸", "8"),
        arrayOf("۹", "9"))
    for (i in digits) {
        getStr = getStr.replace(i[0],i[1])
    }
    return getStr
}