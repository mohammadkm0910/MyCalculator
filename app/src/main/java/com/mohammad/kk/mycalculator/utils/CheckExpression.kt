package com.mohammad.kk.mycalculator.utils

class CheckExpression {
    companion object {
        fun isOperatorLastIndex(str: String):Boolean {
            return str.endsWith("+") || str.endsWith("-") || str.endsWith("×") ||
                    str.endsWith("÷") || str.endsWith("^")
        }
        fun isParenthesisLastIndex(str: String):Boolean {
            return str.endsWith("(") || str.endsWith(")")
        }
        fun isPointIndex(str: String):Boolean {
            return str.endsWith(".")
        }
    }
}