package com.funyaani.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.funyaani.todolist.domain.Category
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "todos")
data class ToDoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: LocalDate,
    val time: LocalTime,
    val category: Category,
    val previousCategory: Category? = Category.DEFAULT, // New property to store the previous category
    val isCompleted: Boolean = false
)
