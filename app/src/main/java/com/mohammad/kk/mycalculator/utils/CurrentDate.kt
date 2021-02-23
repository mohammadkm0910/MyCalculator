package com.mohammad.kk.mycalculator.utils

import java.text.SimpleDateFormat
import java.util.*

class CurrentDate {
    companion object {
        const val PATTERN_DEFAULT_DATE = "yyyy-MM-dd HH:mm:ss"
        fun nowGregorianDate(pattern: String):String
        {
            val sdf = SimpleDateFormat(pattern,Locale.ENGLISH)
            return sdf.format(Date())
        }
    }
}