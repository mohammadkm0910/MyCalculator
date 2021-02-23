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
                if (value.startsWith(".")) {
                    edtText.setText("0.")
                }
                if (value.startsWith("0") && !value.startsWith("0.")) {
                    edtText.setText("")
                }
                val str = edtText.text.toString().replace(",".toRegex(), "")
                if (value != "") edtText.setText(onlyNumber(str))
            }
            edtText.addTextChangedListener(this)
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
            edtText.addTextChangedListener(this)
        }
    }
}