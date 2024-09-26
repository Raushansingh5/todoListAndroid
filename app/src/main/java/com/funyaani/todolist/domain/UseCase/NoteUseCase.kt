package com.funyaani.todolist.domain.UseCase

data class NoteUseCase(
    val addTodoUseCase: AddTodoUseCase,
    val deleteTodoUseCase: DeleteTodoUseCase,
    val getTodosUseCase: GetTodosUseCase,
    val getTodoById: GetTodoById,
    val searchTodo:SearchTodosUseCase
)
