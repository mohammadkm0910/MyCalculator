package com.mohammad.kk.mycalculator.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.EditText
import android.widget.TextView
import com.mohammad.kk.mycalculator.R
import kotlin.math.min

@SuppressLint("AppCompatCustomView")
class ResizingEditText : EditText {
    private val mTempPaint: Paint = TextPaint()
    private var mMaximumTextSize = 0f
    private var mMinimumTextSize = 0f
    private var mStepTextSize = 0f
    private var mWidthConstraint = -1
    private var mHeightConstraint = -1
    private var mOnTextSizeChangeListener: OnTextSizeChangeListener? = null

    constructor(context: Context) : super(context) {
        setUp(context, null)
    }
    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
        setUp(context, attr)
    }
    private fun setUp(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs, R.styleable.ResizingEditText, 0, 0)
            mMaximumTextSize = a.getDimension(
                R.styleable.ResizingEditText_maxTextSize, textSize)
            mMinimumTextSize = a.getDimension(
                R.styleable.ResizingEditText_minTextSize, textSize)
            mStepTextSize = a.getDimension(R.styleable.ResizingEditText_stepTextSize,
                (mMaximumTextSize - mMinimumTextSize) / 3)
            a.recycle()
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mMaximumTextSize)
            minimumHeight = (mMaximumTextSize * 1.2).toInt() + paddingBottom + paddingTop
        }
    }
    override fun setText(text: CharSequence, type: BufferType) {
        super.setText(text, type)
        invalidateTextSize()
    }
    private fun invalidateTextSize() {
        val oldTextSize = textSize
        val newTextSize = getVariableTextSize(text.toString())
        if (oldTextSize != newTextSize)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize)

    }
    override fun setTextSize(unit: Int, size: Float) {
        val oldTextSize = textSize
        super.setTextSize(unit, size)
        if (mOnTextSizeChangeListener != null && textSize != oldTextSize)
            mOnTextSizeChangeListener!!.onTextSizeChanged(this, oldTextSize)
    }
    fun setOnTextSizeChangeListener(listener: OnTextSizeChangeListener?) {
        mOnTextSizeChangeListener = listener
    }
    private fun getVariableTextSize(text: String): Float {
        if (mWidthConstraint < 0 || mMaximumTextSize <= mMinimumTextSize)
            return textSize

        val exponents = countOccurrences(text, '^')
        var lastFitTextSize = mMinimumTextSize
        while (lastFitTextSize < mMaximumTextSize) {
            val nextSize = min(lastFitTextSize + mStepTextSize, mMaximumTextSize)
            mTempPaint.textSize = nextSize
            lastFitTextSize = if (mTempPaint.measureText(text) > mWidthConstraint) {
                break
            } else if (nextSize + nextSize * exponents / 2 > mHeightConstraint) {
                break
            } else {
                nextSize
            }
        }
        return lastFitTextSize
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidthConstraint = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        mHeightConstraint = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getVariableTextSize(text.toString()))
    }
    interface OnTextSizeChangeListener {
        fun onTextSizeChanged(textView: TextView?, oldSize: Float)
    }
    companion object {
        fun countOccurrences(haystack: String, needle: Char): Int {
            var count = 0
            for (element in haystack) {
                if (element == needle) {
                    count++
                }
            }
            return count
        }
    }
}