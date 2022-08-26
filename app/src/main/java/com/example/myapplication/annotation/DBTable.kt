package com.example.myapplication.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DBTable(val tableName: String, val tableVersion: Int = -1)
