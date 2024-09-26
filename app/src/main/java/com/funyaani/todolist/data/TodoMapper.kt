package com.funyaani.todolist.data

import com.funyaani.todolist.domain.Category
import com.funyaani.todolist.domain.Todo

fun ToDoItemEntity.toDomain(): Todo {
    return Todo(
        id = id,
        title = title,
        date = date,
        time = time,
        category = category,
        isCompleted = isCompleted,
        previousCategory = previousCategory?:Category.DEFAULT
    )
}

fun Todo.toEntity(): ToDoItemEntity {
    return ToDoItemEntity(
        id = id,
        title = title,
        date = date,
        time = time,
        category = category,
        isCompleted = isCompleted,
        previousCategory = previousCategory
    )
}
