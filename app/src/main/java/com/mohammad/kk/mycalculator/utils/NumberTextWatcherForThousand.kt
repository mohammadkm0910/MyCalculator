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
            edtText.removeTextChangedListener(this)
            val value = edtText.text.toString()
            if (value != "") {
                val str = edtText.text.toString().replace(",".toRegex(), "")
                val str2 = CheckExpression.replaceSingleParenthesis(str)
                val str3 = onlyNumber(str2)
                if (value != "") edtText.setText(str3)
                edtText.setSelection(str3.length)
            }
            edtText.addTextChangedListener(this)
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
            edtText.addTextChangedListener(this)
        }
    }
}