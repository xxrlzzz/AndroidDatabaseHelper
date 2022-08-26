package com.example.myapplication.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class DBField(
    val fieldName: String,
    @DBFieldType val fieldType: String,
    val version: Int = -1
)
