package com.mohammad.kk.mycalculator.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.PageTransformer
import com.mohammad.kk.mycalculator.R
import kotlin.math.max

@SuppressLint("ViewConstructor")
class SlideViewPager : ViewPager {
    private var padMargin = 0F
    private val staticPagerAdapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return childCount
        }
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return getChildAt(position)
        }
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            removeViewAt(position)
        }
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }
        override fun getPageWidth(position: Int): Float {
            return  if (position == 1) 7.0f / 9.0f else 1.0F
        }
    }
    private val onPageChangeListener : OnPageChangeListener = object : SimpleOnPageChangeListener() {
        private fun recursivelySetEnabled(view: View, enabled: Boolean) {
            if (view is ViewGroup) {
                for (childIndex in 0 until view.childCount) {
                    recursivelySetEnabled(view.getChildAt(childIndex), enabled)
                }
            } else {
                view.isEnabled = enabled
            }
        }
        override fun onPageSelected(position: Int) {

            if (adapter === staticPagerAdapter) {
                for (childIndex in 0 until childCount) {
                    recursivelySetEnabled(getChildAt(childIndex), childIndex == position)
                }
            }
        }
    }
    private val pageTransformer = PageTransformer { view, position ->
            if (position < 0.0f) {
                view.translationX = width * -position
                view.alpha = max(1.0f + position, 0.0f)
            } else {
                view.translationX = 0.0f
                view.alpha = 1.0f
            }
        }
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        adapter = staticPagerAdapter
        setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent))
        addOnPageChangeListener(onPageChangeListener)
        setPageTransformer(false,pageTransformer)
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs,R.styleable.SlideViewPager,0,0)
            padMargin = a.getDimension(R.styleable.SlideViewPager_padMargin,padMargin)
            a.recycle()
        }
        pageMargin = padMargin.toInt()
    }
    override fun onFinishInflate() {
        super.onFinishInflate()
        if (adapter == staticPagerAdapter)
            staticPagerAdapter.notifyDataSetChanged()
    }
}