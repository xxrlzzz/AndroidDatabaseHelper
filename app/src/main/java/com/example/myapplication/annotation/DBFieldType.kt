package com.example.myapplication.annotation

import androidx.annotation.StringDef

@StringDef(
    "TEXT",
    "NUMERIC",
    "INTEGER",
    "REAL",
    "BLOB"
)
annotation class DBFieldType()
