package com.example.myapplication.database

import com.example.myapplication.annotation.DBField
import com.example.myapplication.annotation.DBTable

@DBTable(tableName = "user", tableVersion = 5)
class UserDB(@DBField(fieldName = "b", fieldType = "integer", version = 5) val b: Int = 0) {
    @DBField(fieldName = "a", fieldType = "integer", version = 1)
    val a = 0

    @DBField(fieldName = "c", fieldType = "text", version = 5)
    val c = "123"
}