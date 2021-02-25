package com.mohammad.kk.mycalculator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mohammad.kk.mycalculator.R
import com.mohammad.kk.mycalculator.models.CalcData
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

class AdapterExpressionRecycler(private var ctx: Context, private var calcDataLists:MutableList<CalcData>) : RecyclerView.Adapter<AdapterExpressionRecycler.MyHolder>() {
    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textInputDb:TextView = itemView.findViewById(R.id.textInputDb)
        val textResultDb:TextView = itemView.findViewById(R.id.textResultDb)
        val textCreatedAtDb:TextView = itemView.findViewById(R.id.textCreatedAtDb)
        fun getPersianCal(time:String):String {
            val arrayCal = time.split(" ")
            val arrayShortCal = arrayCal[0].split("-")
            val simplePersianDate = PersianDate()
            simplePersianDate.grgYear = arrayShortCal[0].toInt()
            simplePersianDate.grgMonth = arrayShortCal[1].toInt()
            simplePersianDate.grgDay = arrayShortCal[2].toInt()
            val persianDate = PersianDateFormat("Y/m/d").format(simplePersianDate)
            return String.format("%s %s",persianDate,arrayCal[1])
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.history_calc_list,parent,false)
        return MyHolder(view)
    }
    override fun getItemCount(): Int = calcDataLists.size
    fun removeAt(position: Int) {
        calcDataLists.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, calcDataLists.size)
    }
    fun removeAll() {
        calcDataLists.clear()
        notifyDataSetChanged()
    }
    fun restoreAt(calcData: CalcData, position: Int) {
        calcDataLists.add(position,calcData)
        notifyItemInserted(position)
    }
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.textInputDb.text = calcDataLists[position].input
        holder.textResultDb.text = calcDataLists[position].result
        holder.textCreatedAtDb.text = holder.getPersianCal(calcDataLists[position].createdAt)
    }
}