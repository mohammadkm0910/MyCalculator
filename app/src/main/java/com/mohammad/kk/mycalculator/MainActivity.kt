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
import androidx.viewpager.widget.ViewPager
import com.mohammad.kk.mycalculator.database.RecordExpression
import com.mohammad.kk.mycalculator.utils.*
import com.mohammad.kk.mycalculator.utils.circle_1
import com.mohammad.kk.mycalculator.utils.circle_4
import com.mohammad.kk.mycalculator.utils.getDecimalFormattedString
import com.mohammad.kk.mycalculator.views.ResizingEditText
import com.mohammad.kk.mycalculator.views.SlideViewPager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.pad_calculator.*
import kotlinx.android.synthetic.main.display.*
import kotlinx.android.synthetic.main.pad_advanced.*
import org.mariuszgromada.math.mxparser.Expression


class MainActivity : AppCompatActivity() {
    private lateinit var recordExpression: RecordExpression
    private var currentState = CalculatorState.DEFAULT
    private var isDot = false
    private fun getInput(): String = edtInput.text.toString()
    private fun getOutput(): String = edtOutput.text.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recordExpression = RecordExpression(this)
        setSupportActionBar(actionbarApp)
        slideViewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    callBackSlide.animate().rotation(180F).start()
                } else {
                    callBackSlide.animate().rotation(0F).start()
                }
            }
        })
        callBackSlide.setOnClickListener {
            if (slideViewPager.currentItem == 0)
                slideViewPager.currentItem = 1
            else
                slideViewPager.currentItem = 0
        }
        appendParentheses()
        setStateDisplay()
        clearDisplay()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("currentState", currentState)
        outState.putBoolean("isDot", isDot)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentState = savedInstanceState.getSerializable("currentState") as CalculatorState
        isDot = savedInstanceState.getBoolean("isDot")
        setState(currentState)
    }
    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            callBackSlide.visibility = View.VISIBLE
        } else {
            callBackSlide.visibility = View.GONE
        }
    }

    private fun updateText(strToAdd: String) {
        edtInput.setText(String.format("%s%s", getInput(), strToAdd))
    }

    private fun setStateDisplay() {
        edtInput.showSoftInputOnFocus = false
        edtInput.setTextIsSelectable(false)
        edtInput.inputType = InputType.TYPE_NULL
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.openHistoryActivity -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.openAboutUs-> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }

        }
        return super.onContextItemSelected(item)
    }

    fun appendNumber(view: View) {
        val digits = (view as Button).text.toString()
        if (CheckExpression.isParenthesisLastIndex(getInput(), 2) || CheckExpression.isPercentLastIndex(getInput()) ||
                CheckExpression.isCircleDigitLastIndex(getInput())) {
            updateText("×$digits")
        } else {
            updateText(digits)
        }
    }

    fun appendOperator(view: View) {
        isDot = false
        val opr = (view as Button).text.toString()
        if (getInput().isNotEmpty()) {
            if ((CheckExpression.isPercentLastIndex(getInput()) || CheckExpression.isPercentLastIndex(getInput().substring(0,getInput().length-1))) ||
                    (CheckExpression.isCircleDigitLastIndex(getInput()) || CheckExpression.isCircleDigitLastIndex(getInput().substring(0,getInput().length-1))) ||
                    (CheckExpression.isParenthesisLastIndex(getInput(),2) || CheckExpression.isParenthesisLastIndex(getInput().substring(0,getInput().length-1),2))){
                if (!CheckExpression.isOperatorLastIndex(getInput())) {
                    updateText(opr)
                } else {
                    edtInput.setText(getInput().substring(0, getInput().length - 1))
                    updateText(opr)
                }
            } else if (!CheckExpression.isOperatorNotMinusLastIndex(getInput()) && !getInput().endsWith("-")) {
                if (CheckExpression.isDotLastIndex(getInput())) {
                    edtInput.setText(getInput().substring(0, getInput().length - 1))
                    updateText(opr)
                } else if (getInput().last().isDigit() || CheckExpression.isCircleDigitLastIndex(getInput()) ||
                        CheckExpression.isParenthesisLastIndex(getInput(), 2) || CheckExpression.isPercentLastIndex(getInput())) {
                    updateText(opr)
                }
            } else {
                if (opr == "-") {
                    if (!getInput().endsWith("-")) {
                        updateText("-")
                    }
                } else {
                    if (getInput().endsWith("-")) {
                        if (CheckExpression.isOperatorAppendedMinusLastIndex(getInput())) {
                            edtInput.setText(getInput().substring(0, getInput().length - 2))
                        } else {
                            edtInput.setText(getInput().substring(0, getInput().length - 1))
                        }
                        updateText(opr)
                    } else {
                        edtInput.setText(getInput().substring(0, getInput().length - 1))
                        updateText(opr)
                    }
                }
            }
        }
    }
    private fun appendParentheses() {
        isDot = false
        btnOpenParentheses.setOnClickListener {
            if (getInput().isNotEmpty() && getInput().last().isDigit()) {
                updateText("×(")
            } else if (CheckExpression.isDotLastIndex(getInput())) {
                var backSpace = getInput().substring(0, getInput().length - 1)
                backSpace += "×("
                edtInput.setText(backSpace)
            } else {
                updateText("(")
            }
        }
        btnCloseParentheses.setOnClickListener {
            if ((getInput().isNotEmpty() && getInput().last().isDigit()) || CheckExpression.isPercentLastIndex(getInput()) ||
                    CheckExpression.isCircleDigitLastIndex(getInput()) || CheckExpression.isParenthesisLastIndex(getInput(), 2)) {
                updateText(")")
            } else if (CheckExpression.isDotLastIndex(getInput())) {
                updateText("0)")
            }
        }
    }
    fun appendDot(view: View) {
        if (!isDot) {
            isDot = true
            when {
                getInput().isEmpty() -> {
                    updateText("0.")
                }
                CheckExpression.isOperatorLastIndex(getInput()) -> {
                    updateText("0.")
                }
                CheckExpression.isParenthesisLastIndex(getInput(), 1) -> {
                    updateText("0.")
                }
                CheckExpression.isParenthesisLastIndex(getInput(), 2) -> {
                    updateText("×0.")
                }
                CheckExpression.isCircleDigitLastIndex(getInput()) -> {
                    updateText("×0.")
                }
                CheckExpression.isParenthesisLastIndex(getInput()) -> {
                    updateText("×0.")
                }
                else -> {
                    updateText(".")
                }
            }
        }
    }
    fun appendPercent(view: View) {
        if (getInput().isNotEmpty()) {
            if (getInput().last().isDigit() || CheckExpression.isDotLastIndex(getInput()) || CheckExpression.isPercentLastIndex(getInput())) {
                if (!CheckExpression.isPercentLastIndex(getInput())) {
                    if (CheckExpression.isDotLastIndex(getInput())) {
                        updateText("0%")
                    } else {
                        updateText("%")
                    }
                } else {
                    edtInput.setText(getInput().substring(0, getInput().length - 1))
                    updateText("%")
                }
            } else {
                when {
                    getInput().endsWith("%×") -> {
                        edtInput.setText(getInput().substring(0, getInput().length - 2))
                        updateText("%")
                    }
                    CheckExpression.isOperatorAppendedMinusLastIndex(getInput()) -> {
                        edtInput.setText(getInput().substring(0, getInput().length - 2))
                        updateText("%")
                    }
                    CheckExpression.isPercentAppendedOperatorLastIndex(getInput()) -> {
                        edtInput.setText(getInput().substring(0, getInput().length - 2))
                        updateText("%")
                    }
                    CheckExpression.isOperatorLastIndex(getInput()) -> {
                        edtInput.setText(getInput().substring(0, getInput().length - 1))
                        updateText("%")
                    }
                    else -> {
                        updateText("%")
                    }
                }
            }
        }
    }
    fun getEqualResult(view: View) {
        if (getInput().isNotEmpty()) {
            var userExp = CheckExpression.replaceSingleReverseParenthesis(getInput())
            userExp = userExp.replace("÷", "/")
            userExp = userExp.replace("×", "*")
            userExp = userExp.replace("+-", "-")
            userExp = userExp.replace(",", "")
            try {
                val exp = Expression(userExp)
                val result = exp.calculate().toBigDecimal()
                val edtArrays = arrayOf(getInput(), getDecimalFormattedString(result.toString()))
                edtInput.setText(getDecimalFormattedString(result.toString()))
                setState(CalculatorState.RESULT)
                recordExpression.saveExpression(edtArrays[0], edtArrays[1])
                edtOutput.setText("")
            } catch (e: Exception) {
                setState(CalculatorState.ERROR)
                edtOutput.setText(getString(R.string.default_error))
            }
        }
    }

    private fun setState(state: CalculatorState) {
        when (state) {
            CalculatorState.DEFAULT -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.blue_a200)
                currentState = CalculatorState.DEFAULT
                btnClear.setImageResource(R.drawable.ic_backspace)

                edtOutput.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
            CalculatorState.RESULT -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.light_green_400)
                btnClear.setImageResource(R.drawable.ic_c)
                edtOutput.setTextColor(ContextCompat.getColor(this, R.color.black))
                currentState = CalculatorState.RESULT
            }
            CalculatorState.ERROR -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.red_a400)
                edtOutput.setTextColor(ContextCompat.getColor(this, R.color.red_500))
                btnClear.setImageResource(R.drawable.ic_c)
                currentState = CalculatorState.ERROR
            }
        }
    }

    private fun clearDisplay() {

        btnClear.setOnClickListener {
            if (getInput().isNotEmpty()) {
                if (CheckExpression.isDotLastIndex(getInput())) {
                    isDot = false
                }
                if (currentState == CalculatorState.RESULT || currentState == CalculatorState.ERROR)
                    edtInput.setText("")
                else {
                    when {
                        CheckExpression.isCircleDigitLastIndex(getInput()) -> {
                            val lastP = getInput().last().toString()
                            val subLastP = CheckExpression.replaceSingleReverseParenthesis(lastP).substring(0, 2)
                            edtInput.setText(getInput().substring(0, getInput().length - 1))
                            updateText(subLastP)
                        }
                        getInput().endsWith("0.") -> {
                            edtInput.setText(getInput().substring(0, getInput().length - 2))
                        }
                        else -> {
                            edtInput.setText(getInput().substring(0, getInput().length - 1))
                        }
                    }
                }
            }
        }
        btnClear.setOnLongClickListener {
            edtInput.setText("")
            edtOutput.setText("")
            isDot = false
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
            var userExp = CheckExpression.replaceSingleReverseParenthesis(getInput())
            userExp = userExp.replace("÷", "/")
            userExp = userExp.replace("×", "*")
            userExp = userExp.replace("+-", "-")
            userExp = userExp.replace(",", "")
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
        ERROR, RESULT, DEFAULT
    }
}