package com.funyaani.todolist.domain

import com.funyaani.todolist.data.ToDoItemEntity
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodo():Flow<List<Todo>>
    suspend fun addTodo(todo: Todo)
    fun getTodos(category: Category): Flow<List<Todo>>
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(id: Long)
//    suspend fun searchTodos(query: String): List<Todo>
//    fun getOverdueTodos(): Flow<List<Todo>>
    suspend fun getTodoById(id: Long): Todo?
    suspend fun searchTodos(query: String): List<Todo>
    suspend fun markTodoAsCompleted(todoId: Long)

//     New method to mark a todo item as incomplete
    suspend fun markTodoAsIncomplete(todoId: Long)
}


