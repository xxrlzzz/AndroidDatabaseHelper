package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.concurrent.ConcurrentHashMap

/**
 * a wrapper of SQLiteOpenHelper
 */
class DBManager(
    ctx: Context,
    val name: String,
    factory: SQLiteDatabase.CursorFactory?,
    val version: Int
) : SQLiteOpenHelper(ctx, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        private val instanceMap = ConcurrentHashMap<String, DBManager>()
        fun getInstance(ctx: Context, name: String): DBManager {
            var instance = instanceMap[name]
            if (instance == null) {
                instance = DBManager(ctx, name, null, 1)
                instanceMap[name] = instance
            }
            return instance
        }
    }
}