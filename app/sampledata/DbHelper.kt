package ru.netology.nmedia.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// DDLs - массив команд с SQL-запросами
class DbHelper(context: Context, dbVersion: Int, dbName: String, private val DDLs: Array<String>) :
    SQLiteOpenHelper(context, dbName, null, dbVersion) {
    override fun onCreate(db: SQLiteDatabase) {
        DDLs.forEach {
            db.execSQL(it)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not implemented")
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not implemented")
    }
}