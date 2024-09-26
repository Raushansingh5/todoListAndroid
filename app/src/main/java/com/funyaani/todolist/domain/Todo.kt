package com.funyaani.todolist.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

import java.time.LocalDate
import java.time.LocalTime


data class Todo(
    val id: Long = 0,
    val title: String,
    val date: LocalDate,
    val time: LocalTime,
    val category: Category,
    val previousCategory: Category=Category.DEFAULT, // New property to store the previous category
    val isCompleted: Boolean = false
)

class InvalidNoteException(message: String): Exception(message)
