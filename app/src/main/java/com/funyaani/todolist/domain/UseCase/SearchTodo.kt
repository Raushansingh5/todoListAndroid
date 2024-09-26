package com.funyaani.todolist.domain.UseCase

import com.funyaani.todolist.domain.Todo
import com.funyaani.todolist.domain.TodoRepository
import javax.inject.Inject


class SearchTodosUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(query: String): List<Todo> {
        return repository.searchTodos(query)
    }
}
