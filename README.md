
# AndroidDatabaseHelper

A sample project use annotations to manager sqlite tables

## usage

Add @DBTable for table class and @DBField for table filed  

```kotlin

@DBTable(tableName = "user", tableVersion = 5)
class UserDB(@DBField(fieldName = "b", fieldType = "integer", version = 5) val b: Int = 0) {
    @DBField(fieldName = "a", fieldType = "integer", version = 1)
    val a = 0

    @DBField(fieldName = "c", fieldType = "text", version = 5)
    val c = "123"
}
```

Init when app start, and get associated table manager from it.
```kotlin
DBService.getInstance(baseContext, listOf(UserDB::class.java))

musicTableManager = DBService.getInstance()?.getTableManager("music")!!
```
