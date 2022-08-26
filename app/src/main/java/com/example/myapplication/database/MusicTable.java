package com.example.myapplication.database;

import android.content.ContentValues;

import com.example.myapplication.annotation.DBField;
import com.example.myapplication.annotation.DBTable;

@DBTable(tableName = "music")
public class MusicTable {
    @DBField(fieldName = "a", fieldType = "int")
    public int a;

    public ContentValues intoContentValues() {
        ContentValues values = new ContentValues();
        values.put("a", a);
        return values;
    }
}
