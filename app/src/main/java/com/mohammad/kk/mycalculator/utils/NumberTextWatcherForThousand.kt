package com.mohammad.kk.mycalculator.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class NumberTextWatcherForThousand(private var edtText: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }
    override fun afterTextChanged(s: Editable) {
        try {
            EDITABLE = s.toString()
            edtText.removeTextChangedListener(this)
            val value = edtText.text.toString()
            if (value != "") {
                val str = edtText.text.toString().replace(",".toRegex(), "")
                if (value != "") edtText.setText(CheckExpression.replaceSingleParenthesis(onlyNumber(str)))
                edtText.setSelection(CheckExpression.replaceSingleParenthesis(onlyNumber(str)).length)
            }
            edtText.addTextChangedListener(this)
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
            edtText.addTextChangedListener(this)
        }
    }
    companion object {
        var EDITABLE = ""
    }
}