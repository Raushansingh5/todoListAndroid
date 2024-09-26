package com.funyaani.todolist.presentation.utils

import com.funyaani.todolist.domain.Category
import com.funyaani.todolist.domain.Todo
import java.time.LocalDate
import java.time.LocalTime

sealed class TaskEvent {
    data class TitleChanged(val title: String) : TaskEvent()
    data class DueDateChanged(val dueDate: LocalDate?) : TaskEvent()
    data class DueTimeChanged(val dueTime: LocalTime?) : TaskEvent()
//    data class RepeatFrequencyChanged(val repeatFrequency: String) : TaskEvent()
    data class CategoryChanged(val category: Category) : TaskEvent()
    object SaveTask : TaskEvent()


}