package com.mohammad.kk.mycalculator

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mohammad.kk.mycalculator.adapters.AdapterExpressionRecycler
import com.mohammad.kk.mycalculator.database.RecordExpression
import com.mohammad.kk.mycalculator.models.CalcData
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.danger_dialog.view.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var recordExpression : RecordExpression
    private var expDataList:ArrayList<CalcData> = ArrayList()
    private lateinit var adapterExpressionRecycler:AdapterExpressionRecycler
    private val p = Paint()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(actionbarHistory)
        recordExpression = RecordExpression(this)
        val list = recordExpression.getExpression()
        while (list.moveToNext()) {
            expDataList.add(
                CalcData(
                    list.getString(0),
                    list.getString(1),
                    list.getString(2),
                    list.getString(3)
                )
            )
        }
        adapterExpressionRecycler = AdapterExpressionRecycler(this,expDataList)
        listHistoryExpression.layoutManager = LinearLayoutManager(this)
        listHistoryExpression.adapter = adapterExpressionRecycler
        enableSwipe()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_history,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.clearAllHistory -> {
                val builder = AlertDialog.Builder(this)
                val viewDialogDanger = LayoutInflater.from(this).inflate(R.layout.danger_dialog,findViewById(R.id.dangerDialogContainer))
                builder.setView(viewDialogDanger)
                val dialog = builder.create()
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                viewDialogDanger.fabClose.setOnClickListener {
                    dialog.dismiss()
                }
                viewDialogDanger.fabDone.setOnClickListener {
                    recordExpression.deleteAllExpression()
                    adapterExpressionRecycler.removeAll()
                    Snackbar.make(listHistoryExpression,"All computing history deleted !!",Snackbar.LENGTH_LONG).show()
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun enableSwipe() {
        val simpleItemTouchBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                    val deletedModel = expDataList[position]
                    recordExpression.deleteExpression(deletedModel.id)
                    adapterExpressionRecycler.removeAt(position)
                    val snackBar = Snackbar.make(
                        listHistoryExpression,
                        "removed from is list",
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction("UNDO") {
                        recordExpression.saveExpression(deletedModel.id.toInt(),deletedModel.input,deletedModel.result,deletedModel.createdAt)
                        adapterExpressionRecycler.restoreAt(deletedModel,position)
                    }
                    snackBar.setActionTextColor(Color.YELLOW)
                    snackBar.show()
                }
            }
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val icon:Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3
                    if (dX > 0) {
                        p.color = Color.parseColor("#f44336")
                        val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background,p)
                        icon = ContextCompat.getDrawable(this@HistoryActivity,R.drawable.ic_trash)!!.toBitmap()
                        val iconR = RectF(
                            itemView.left.toFloat() + width,
                            itemView.top.toFloat() + width,
                            itemView.left.toFloat() + 2 * width,
                            itemView.bottom.toFloat() - width
                        )
                        c.drawBitmap(icon, null, iconR, p)
                    } else {
                        p.color = Color.parseColor("#4CAF50")
                        val background = RectF(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                        )
                        c.drawRect(background, p)
                        icon = ContextCompat.getDrawable(this@HistoryActivity,R.drawable.ic_trash)!!.toBitmap()
                        val iconR = RectF(
                            itemView.right.toFloat() - 2 * width,
                            itemView.top.toFloat() + width,
                            itemView.right.toFloat() - width,
                            itemView.bottom.toFloat() - width
                        )
                        c.drawBitmap(icon, null, iconR, p)
                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchBack)
        itemTouchHelper.attachToRecyclerView(listHistoryExpression)
    }
}