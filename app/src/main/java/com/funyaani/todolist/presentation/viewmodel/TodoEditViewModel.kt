package com.funyaani.todolist.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funyaani.todolist.domain.Category
import com.funyaani.todolist.domain.InvalidNoteException
import com.funyaani.todolist.domain.Todo
import com.funyaani.todolist.domain.UseCase.NoteUseCase
import com.funyaani.todolist.presentation.utils.TaskEvent
import com.funyaani.todolist.presentation.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ToDoEditViewModel @Inject constructor(
    private val noteUseCase: NoteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ToDoEditState())
    val state: StateFlow<ToDoEditState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentTodoId: Long? = null

    init {
        savedStateHandle.get<Long>("todoId")?.let { todoId ->
            if (todoId != -1L) {
                viewModelScope.launch {
                    noteUseCase.getTodoById(todoId)?.also { todo ->
                        currentTodoId = todo.id
                        _state.value = _state.value.copy(
                            title = todo.title,
                            date = todo.date,
                            time = todo.time,
                            category = todo.category,
                            isCompleted = todo.isCompleted
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.TitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
            }
            is TaskEvent.DueDateChanged -> {
                _state.value = _state.value.copy(date = event.dueDate)
            }
            is TaskEvent.DueTimeChanged -> {
                _state.value = _state.value.copy(time = event.dueTime)
            }
            is TaskEvent.CategoryChanged -> {
                _state.value = _state.value.copy(category = event.category)
            }
            is TaskEvent.SaveTask -> {
                viewModelScope.launch {
                    try {
                        noteUseCase.addTodoUseCase(
                            Todo(
                                id = currentTodoId ?: 0,
                                title = _state.value.title,
                                date = _state.value.date ?: LocalDate.now(),
                                time = _state.value.time ?: LocalTime.now(),
                                category = _state.value.category,
                                isCompleted = _state.value.isCompleted
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveEvent)
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                    }
                }
            }
        }
    }
}
