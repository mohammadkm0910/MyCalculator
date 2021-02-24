package com.mohammad.kk.mycalculator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mohammad.kk.mycalculator.database.RecordExpression
import com.mohammad.kk.mycalculator.utils.NumberTextWatcherForThousand
import com.mohammad.kk.mycalculator.utils.getDecimalFormattedString
import com.mohammad.kk.mycalculator.views.ResizingEditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.pad_calculator.*
import kotlinx.android.synthetic.main.display.*
import org.mariuszgromada.math.mxparser.Expression


class MainActivity : AppCompatActivity() {
    private lateinit var recordExpression: RecordExpression
    private var currentState = CalculatorState.DEFAULT
    private fun getInput():String = edtInput.text.toString()
    private fun getOutput():String = edtOutput.text.toString()
    private fun commaCount():Int {
        var count = 0
        for (i in getInput()){
            if (i == ',') ++count
        }
        return count
    }
    private fun editableCommaCount(): Int {
        var count = 0
        for (i in NumberTextWatcherForThousand.EDITABLE) {
            if (i == ',') ++count
        }
        return count
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recordExpression = RecordExpression(this)
        setSupportActionBar(actionbarApp)
        setStateDisplay()
        onClickBtnCalculator()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("currentState",currentState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentState = savedInstanceState.getSerializable("currentState") as CalculatorState
        setState(currentState)
    }
    private fun updateText(strToAdd: String) {
        val cursorPos = edtInput.selectionStart
        val leftStr = getInput().substring(0,cursorPos)
        val rightStr = getInput().substring(cursorPos)
        edtInput.setText(String.format("%s%s%s",leftStr,strToAdd,rightStr))
        if (leftStr.endsWith(",") && !strToAdd[0].isDigit()) return
        if (editableCommaCount() == commaCount() || !strToAdd[0].isDigit()) {
            edtInput.setSelection(cursorPos + 1)
        }  else {
            edtInput.setSelection(cursorPos + 2)
        }
    }
    private fun setStateDisplay() {
        edtInput.showSoftInputOnFocus = false
        edtInput.addTextChangedListener(NumberTextWatcherForThousand(edtInput))
        edtInput.setOnTextSizeChangeListener(object : ResizingEditText.OnTextSizeChangeListener {
            override fun onTextSizeChanged(textView: TextView?, oldSize: Float) {
                val textScale = oldSize / textView!!.textSize
                val animatorSet = AnimatorSet()
                animatorSet.playTogether(
                    ObjectAnimator.ofFloat(textView, View.SCALE_X, textScale, 1.0f),
                    ObjectAnimator.ofFloat(textView, View.SCALE_Y, textScale, 1.0f),
                    ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, 2.0F, 0.0f),
                    ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, 2.0F, 0.0f)
                )
                animatorSet.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
                animatorSet.interpolator = AccelerateDecelerateInterpolator()
                animatorSet.start()
            }
        })
        edtInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edtOutput.setText(eval())
                setState(CalculatorState.DEFAULT)
            }
        })
        edtOutput.inputType = InputType.TYPE_NULL
    }
    fun appendNumber(view: View) {
        val digits = (view as Button).text.toString()
        updateText(digits)
    }
    fun appendOperator(view: View) {
        val digits = (view as Button).text.toString()
        updateText(digits)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_options,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.openHistoryActivity -> {
                val intent = Intent(this,HistoryActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onContextItemSelected(item)
    }
    private fun setState(state:CalculatorState) {
       when(state) {
           CalculatorState.DEFAULT -> {
               window.statusBarColor = ContextCompat.getColor(this,R.color.blue_a200)
               currentState = CalculatorState.DEFAULT
               clear.visibility = View.GONE
               backspace.visibility = View.VISIBLE
               edtOutput.setTextColor(ContextCompat.getColor(this,R.color.black))
           }
           CalculatorState.RESULT -> {
               window.statusBarColor = ContextCompat.getColor(this,R.color.light_green_400)
               clear.visibility = View.VISIBLE
               backspace.visibility = View.GONE
               edtOutput.setTextColor(ContextCompat.getColor(this,R.color.black))
               currentState = CalculatorState.RESULT
           }
           CalculatorState.ERROR -> {
               window.statusBarColor = ContextCompat.getColor(this,R.color.red_a400)
               edtOutput.setTextColor(ContextCompat.getColor(this,R.color.red_500))
               clear.visibility = View.VISIBLE
               backspace.visibility = View.GONE
               currentState = CalculatorState.ERROR
           }
       }
    }
    private fun onClickBtnCalculator() {
        btnZero.setOnClickListener {
            updateText("0")
        }
        btnPoint.setOnClickListener {
            updateText(".")
        }
        btnExponent.setOnClickListener {
            updateText("^")
        }
        btnParentheses.setOnClickListener {
            var openPar = 0
            var closePar = 0
            for (element in getInput().indices){
                if (getInput()[element] == '(') openPar += 1
                if (getInput()[element] == ')') closePar += 1
            }
            if (openPar == closePar || getInput().endsWith("(")) {
                updateText("(")
            } else if (closePar < openPar && !getInput().endsWith("(")) {
                updateText(")")
            }
        }
        btnEqual.setOnClickListener {
            if (getInput().isNotEmpty()) {
                var userExp = edtInput.text.toString()
                userExp = userExp.replace("÷","/")
                userExp = userExp.replace("×","*")
                userExp = userExp.replace(",","")
                try {
                    val exp = Expression(userExp)
                    val result = exp.calculate().toBigDecimal()
                    val edtArrays = arrayOf(getInput(), getDecimalFormattedString(result.toString()))
                    edtInput.setText(getDecimalFormattedString(result.toString()))
                    edtOutput.setText("")
                    setState(CalculatorState.RESULT)
                    recordExpression.saveExpression(edtArrays[0],edtArrays[1])
                } catch (e: Exception) {
                    setState(CalculatorState.ERROR)
                    edtOutput.setText(getString(R.string.default_error))
                }
            }
        }
        btnClear.setOnClickListener {
            if(getInput().isNotEmpty()){
                if (currentState == CalculatorState.RESULT || currentState == CalculatorState.ERROR)
                    edtInput.setText("")
                else {
                    val cursorPos = edtInput.selectionStart
                    val lastPos = edtInput.length()
                    if (cursorPos != lastPos && cursorPos != 0) {
                        val rightStr = getInput().substring(0,cursorPos)
                        val leftStr = getInput().substring(cursorPos)
                        val newRightStr = rightStr.substring(0,rightStr.length-1)
                        edtInput.setText(String.format("%s%s",newRightStr,leftStr))
                    } else {
                        edtInput.setText(getInput().substring(0,getInput().length-1))
                    }
                    edtInput.setSelection(edtInput.length())
                }
            }
        }
        btnClear.setOnLongClickListener {
            edtInput.setText("")
            edtOutput.setText("")
            true
        }
    }
    override fun onBackPressed() {
        if (slideViewPager == null || slideViewPager.currentItem == 0)
            super.onBackPressed()
        else
            slideViewPager.currentItem = 0
    }
    private fun eval(): String {
        if (getInput().isNotEmpty()) {
            var userExp = edtInput.text.toString()
            userExp = userExp.replace("÷","/")
            userExp = userExp.replace("×","*")
            userExp = userExp.replace(",","")
            return try {
                val exp = Expression(userExp)
                val result = exp.calculate().toBigDecimal()
                getDecimalFormattedString(result.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                getOutput()
            }
        } else {
            return ""
        }
    }
    private enum class CalculatorState {
        ERROR,RESULT,DEFAULT
    }
}