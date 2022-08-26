package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.core.database.sqlite.transaction
import com.example.myapplication.annotation.DBField
import com.example.myapplication.annotation.DBTable
import java.lang.IllegalStateException

/**
 * TableManager auto create/update table,filed by @see{DBField}
 */
class TableManager<T>(private val ctx: Context, clazz: Class<T>, dbName: String) {
    private lateinit var db: SQLiteDatabase
    private var tableName: String = ""
    private var tableVersion: Int = 1
    private var initialized = false

    private var dbFields : List<DBField>? = null

    init {
        val dbManager = DBManager.getInstance(ctx, dbName)
        // init from class annotation
        if (initByAnnotation(clazz)) {
            db = dbManager.writableDatabase

            // check table version
            // and may create table
            checkTable()

            initialized = true
        }
    }

    fun success() : Boolean{
        return initialized
    }

    private fun initByAnnotation(clazz: Class<T>) : Boolean {
        val dbTable = clazz.getAnnotation(DBTable::class.java) ?: return false
        tableName = dbTable.tableName
        tableVersion = dbTable.tableVersion
        dbFields = clazz.declaredFields.mapNotNull {
            it.getAnnotation(DBField::class.java)
        }
        return true
    }

    private fun checkTable() {
        val service = DBTableVersionService.getInstance(ctx)
        val oldVersion = service.getTableVersion(tableName)
        if (oldVersion >= tableVersion && tableVersion != -1) {
            return
        }
        val tableExist = tableExists()
        if (!tableExist) {
            createTable()
        } else if (oldVersion < tableVersion && tableVersion != -1) {
            updateTable(oldVersion, tableVersion)
        }
        if (oldVersion != tableVersion) {
            service.updateTableVersion(tableName, tableVersion)
        }
    }

    private fun createTable() {
        val sql = createTableSQL(tableName, dbFields!!)
        Log.d(TAG, "createTable: $tableName")
        db.execSQL(sql)
    }

    private fun updateTable(oldVersion: Int, newVersion: Int) {

        val newDBFields = dbFields!!.filter {
            it.version in (oldVersion + 1)..newVersion || it.version == -1
        }
        Log.d(TAG, "updateTable: $tableName with ${newDBFields.size} new fields")
        // TODO make sure field not exist.
        val newSQL = updateTableSQL(tableName, newDBFields)
        db.transaction {
            newSQL.forEach {
                db.execSQL(it)
            }
        }
    }

    private fun tableExists(): Boolean {
        if (!db.isOpen) {
            return false
        }

        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
            arrayOf("table", tableName)
        )
        if (!cursor.moveToFirst()) {
            cursor.close()
            return false
        }
        val cnt = cursor.getInt(0)
        cursor.close()
        return cnt > 0
    }

    fun query(selection: String, sortOrder: String? = null, limit: String? = null) : Cursor {
        if (!initialized) {
            throw IllegalStateException("Use tableManager after failed init")
        }
        return db.query(tableName, null, selection, null, null, null, sortOrder, limit)
    }

    fun insert(value: ContentValues) {
        if (!initialized) {
            throw IllegalStateException("Use tableManager after failed init")
        }
        db.transaction {
            db.insert(tableName, null, value)
        }
    }

    fun delete(where: String, args: Array<String>? = null) {
        if (!initialized) {
            throw IllegalStateException("Use tableManager after failed init")
        }
        db.delete(tableName, where, args)
    }

    companion object {
        const val TAG = "TableManager"

        fun createTableSQL(tableName: String, dbFields: List<DBField>): String {
            val sb = StringBuilder()
            sb.append("CREATE TABLE IF NOT EXISTS ")
            sb.append(tableName)
            sb.append(" (_ID INTEGER PRIMARY KEY")
            dbFields.forEach {
                sb.append(",${it.fieldName} ${it.fieldType}")
            }
            sb.append(")")
            return sb.toString()
        }

        fun updateTableSQL(tableName: String, dbFields: List<DBField>): List<String> {
            return dbFields.map {
                "alter table $tableName add column ${it.fieldName} ${it.fieldType}"
            }
        }

        fun dropTableSQL(tableName: String): String {
            return "DROP TABLE IF EXISTS $tableName"
        }
    }
}