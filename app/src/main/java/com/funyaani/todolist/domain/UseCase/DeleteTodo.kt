package com.funyaani.todolist.domain.UseCase

import com.funyaani.todolist.domain.TodoRepository
import javax.inject.Inject

class DeleteTodoUseCase (private val repository: TodoRepository) {
    suspend operator fun invoke(id: Long) {
        repository.deleteTodo(id)
    }
}
