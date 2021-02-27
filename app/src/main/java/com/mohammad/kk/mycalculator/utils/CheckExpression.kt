package com.mohammad.kk.mycalculator.utils

class CheckExpression {
    companion object {
        fun isOperatorLastIndex(str: String):Boolean {
            return str.endsWith("+") || str.endsWith("-") || str.endsWith("×") ||
                    str.endsWith("÷") || str.endsWith("^")
        }
        fun isOperatorNotMinusLastIndex(str: String):Boolean {
            return str.endsWith("+") || str.endsWith("×") ||
                    str.endsWith("÷") || str.endsWith("^")
        }
        fun isOperatorAppendedMinusLastIndex(str: String):Boolean {
            return str.endsWith("+-") || str.endsWith("×-") ||
                    str.endsWith("÷-") || str.endsWith("^-")
        }
        fun isParenthesisLastIndex(str: String,isOpen:Int = 0):Boolean {
            return when (isOpen) {
                0 -> {
                    str.endsWith("(") || str.endsWith(")")
                }
                1 -> {
                    str.endsWith("(")
                }
                else -> {
                    str.endsWith(")")
                }
            }
        }
        fun isDotLastIndex(str: String):Boolean {
            return str.endsWith(".")
        }
        fun isPercentLastIndex(str: String):Boolean {
            return str.endsWith("%")
        }
        fun replaceSingleParenthesis(str: String): String {
            var newStr = str
            newStr = newStr.replace("(1)", circle_1)
            newStr = newStr.replace("(2)", circle_2)
            newStr = newStr.replace("(3)", circle_3)
            newStr = newStr.replace("(5)", circle_5)
            newStr = newStr.replace("(6)", circle_6)
            newStr = newStr.replace("(7)", circle_7)
            newStr = newStr.replace("(8)", circle_8)
            newStr = newStr.replace("(9)", circle_9)
            return newStr
        }
        fun replaceSingleReverseParenthesis(str: String): String {
            var newStr = str
            newStr = newStr.replace(circle_1,"(1)")
            newStr = newStr.replace(circle_2,"(2)")
            newStr = newStr.replace(circle_3,"(3)")
            newStr = newStr.replace(circle_4,"(4)")
            newStr = newStr.replace(circle_5,"(5)")
            newStr = newStr.replace(circle_6,"(6)")
            newStr = newStr.replace(circle_7,"(7)")
            newStr = newStr.replace(circle_8,"(8)")
            newStr = newStr.replace(circle_9,"(9)")
            return newStr
        }
        fun isCircleDigitLastIndex(str: String):Boolean {
            return str.endsWith(circle_1) ||  str.endsWith(circle_2) ||  str.endsWith(circle_3) ||  str.endsWith(circle_4) ||
                    str.endsWith(circle_5) ||  str.endsWith(circle_6) ||  str.endsWith(circle_7) ||  str.endsWith(circle_8) ||
                    str.endsWith(circle_9)
        }
    }
}