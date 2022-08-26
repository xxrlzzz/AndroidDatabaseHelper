package com.example.myapplication

import android.content.Context
import android.util.Log
import com.example.myapplication.annotation.DBTable
import java.util.concurrent.ConcurrentHashMap

/**
 * Cache all TableManager of a database
 */
class DBService(
    private val ctx: Context,
    tableClasses: List<Class<*>>,
    private val dbName: String
) {
    private val tableManagers = ConcurrentHashMap<String, TableManager<*>>()
    private val tableNameMap = ConcurrentHashMap<String, Class<*>>()

    init {
        tableClasses.forEach {
            it.getAnnotation(DBTable::class.java)?.let { dbTable ->
                tableNameMap[dbTable.tableName] = it
            }
        }
    }

    fun getTableManager(tableName: String): TableManager<*>? {
        var manager = tableManagers[tableName]
        if (manager == null) {
            val clazz = tableNameMap[tableName] ?: return null
            manager = TableManager(
                ctx, clazz, dbName
            )
            tableManagers[tableName] = manager
        }
        return manager
    }

    companion object {
        const val TAG = "DBService"
        private val instanceMap = ConcurrentHashMap<String, DBService>()

        @Volatile
        private var defaultDB: String? = null
        fun getInstance(
            ctx: Context,
            tableClasses: List<Class<out Any>>,
            dbName: String? = null
        ): DBService {
            val db =
                dbName ?: defaultDB ?: "${ctx.applicationInfo.name}-db"

            var instance = instanceMap[db]
            if (instance == null) {
                instance = DBService(ctx, tableClasses, db)
                instanceMap[db] = instance
            }
            if (defaultDB == null) {
                Log.d(TAG, "set defaultDB to $db")
                defaultDB = db
            }
            return instance
        }

        fun getInstance(dbName: String? = defaultDB) = instanceMap[dbName]
    }
}