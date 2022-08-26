package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * use shardPreferences to manager version of table
 */
class DBTableVersionService(ctx: Context) {
    private val sharedPreferences: SharedPreferences =
        ctx.getSharedPreferences("table-version", Context.MODE_PRIVATE)

    fun getTableVersion(tableName: String): Int {
        return sharedPreferences.getInt(tableName, 0)
    }

    fun updateTableVersion(tableName: String, version:Int) {
        sharedPreferences.edit {
            this.putInt(tableName, version)
        }
    }

    companion object {
        @Volatile
        private var instance: DBTableVersionService? = null
        fun getInstance(ctx: Context) =
            instance ?: synchronized(DBTableVersionService::class.java) {
                instance ?: DBTableVersionService(ctx).also { instance = it }
            }
    }
}