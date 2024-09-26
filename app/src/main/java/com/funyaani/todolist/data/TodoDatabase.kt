package com.funyaani.todolist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [ToDoItemEntity::class], version = 1,exportSchema = false)
@TypeConverters(CategoryConverter::class, LocalDateTypeConverter::class, LocalTimeTypeConverter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): ToDoDao
}

