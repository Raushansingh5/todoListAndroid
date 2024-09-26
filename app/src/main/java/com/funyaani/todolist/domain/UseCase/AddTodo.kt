package com.funyaani.todolist.domain.UseCase

import com.funyaani.todolist.common.Resource
import com.funyaani.todolist.domain.Category
import com.funyaani.todolist.domain.InvalidNoteException
import com.funyaani.todolist.domain.Todo
import com.funyaani.todolist.domain.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class AddTodoUseCase  (private val repository: TodoRepository) {
    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(todo: Todo) {
        if(todo.title.isBlank()) {
            throw InvalidNoteException("The title of the note can't be empty.")
        }
        if(todo.date.toString()=="") {
            throw InvalidNoteException("The date of the note can't be empty.")
        }
        repository.addTodo(todo)
    }

}






