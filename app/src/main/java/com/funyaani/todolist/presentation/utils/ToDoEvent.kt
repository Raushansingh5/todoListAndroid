package com.funyaani.todolist.presentation.utils

import com.funyaani.todolist.data.ToDoItemEntity
import com.funyaani.todolist.domain.Category
import com.funyaani.todolist.domain.Todo

sealed class ToDoEvent {

    data class DeleteToDoItem(val todo:Todo) : ToDoEvent()
//   // data class SearchTodos(val query: String) : ToDoEvent()

    data class SearchTodos(val query: String) : ToDoEvent()
      object OnUndoDeleteClick : ToDoEvent()
    data class OnCheck(val todo: Todo):ToDoEvent()
    data class OnUnCheck(val todo: Todo):ToDoEvent()






}