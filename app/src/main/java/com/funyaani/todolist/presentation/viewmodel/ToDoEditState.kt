package com.funyaani.todolist.presentation.viewmodel

import com.funyaani.todolist.domain.Category
import java.time.LocalDate
import java.time.LocalTime

data class ToDoEditState(
    val title: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val isCompleted:Boolean=false,
    val category: Category = Category.DEFAULT,
    val errorMessage: String? = null
)

