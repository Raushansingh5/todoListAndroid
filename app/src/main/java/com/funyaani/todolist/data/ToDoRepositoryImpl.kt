package com.funyaani.todolist.data

import com.funyaani.todolist.domain.Category
import com.funyaani.todolist.domain.Todo
import com.funyaani.todolist.domain.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TodoRepositoryImpl (private val dao: ToDoDao) : TodoRepository {

    override fun getAllTodo(): Flow<List<Todo>> {
        return dao.getAllTodos().map { it.map { it.toDomain() } }
    }

    override suspend fun addTodo(todo: Todo) {
        dao.insert(todo.toEntity())
    }

    override fun getTodos(category: Category): Flow<List<Todo>> {
        return dao.getTodosByCategory(category).map { it.map { it.toDomain() } }
    }

    override suspend fun updateTodo(todo: Todo) {
        dao.insert(todo.toEntity())
    }

    override suspend fun deleteTodo(id: Long) {
        dao.deleteTodo(id)
    }

    override suspend fun searchTodos(query: String): List<Todo> {
        return dao.searchTodos(query).map { it.toDomain() }
    }


    override suspend fun getTodoById(id: Long): Todo? {
        return dao.getTodoById(id)?.toDomain()
    }

    // New method to mark a todo item as completed
    override suspend fun markTodoAsCompleted(todoId: Long) {
        dao.markAsCompleted(todoId)
    }

    // New method to mark a todo item as incomplete
    override suspend fun markTodoAsIncomplete(todoId: Long) {
        dao.markAsIncomplete(todoId)
    }
}
