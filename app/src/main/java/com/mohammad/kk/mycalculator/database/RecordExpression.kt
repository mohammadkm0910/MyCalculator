package com.mohammad.kk.mycalculator.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mohammad.kk.mycalculator.R
import com.mohammad.kk.mycalculator.utils.CurrentDate
import java.text.SimpleDateFormat
import java.util.*

class RecordExpression(private var context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_INPUT TEXT NOT NULL, $COL_RESULT TEXT NOT NULL, $COL_CREATED_AT DATETIME NOT NULL);")
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun saveExpression(input: String, result: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_INPUT,input)
        values.put(COL_RESULT,result)
        values.put(COL_CREATED_AT,CurrentDate.nowGregorianDate(CurrentDate.PATTERN_DEFAULT_DATE))
        return try {
            db!!.insert(TABLE_NAME,null,values)
            db.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    fun saveExpression(id: Int,input: String, result: String, created: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID,id)
        values.put(COL_INPUT,input)
        values.put(COL_RESULT,result)
        values.put(COL_CREATED_AT,created)
        return try {
            db!!.insert(TABLE_NAME,null,values)
            db.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    fun getExpression(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_ID DESC; ",null)
    }
    fun deleteExpression(id: String): Boolean {
        val db = this.writableDatabase
        return try {
            db.delete(TABLE_NAME, "$COL_ID=? ", arrayOf(id))
            db.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    fun deleteAllExpression(): Boolean {
        val db = this.writableDatabase
        return try {
            db.execSQL("DELETE FROM $TABLE_NAME")
            db.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "calc_history.db"
        private const val TABLE_NAME = "_expression"
        private const val COL_ID = "id"
        private const val COL_INPUT = "input"
        private const val COL_RESULT = "result"
        private const val COL_CREATED_AT = "created_at"
    }
}