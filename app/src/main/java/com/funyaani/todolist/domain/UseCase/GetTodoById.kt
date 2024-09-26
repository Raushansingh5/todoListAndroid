package com.funyaani.todolist.domain.UseCase

import com.funyaani.todolist.domain.Todo
import com.funyaani.todolist.domain.TodoRepository
import javax.inject.Inject

class GetTodoById (private val repository: TodoRepository) {
    suspend operator fun invoke(id:Long): Todo? {
       return repository.getTodoById(id)
    }
}
