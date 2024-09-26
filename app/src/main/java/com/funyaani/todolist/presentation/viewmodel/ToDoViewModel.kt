package com.funyaani.todolist.presentation.viewmodel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funyaani.todolist.AlarmReceiver
import com.funyaani.todolist.common.Resource
import com.funyaani.todolist.domain.Category
import com.funyaani.todolist.domain.InvalidNoteException
import com.funyaani.todolist.domain.Todo
import com.funyaani.todolist.domain.UseCase.NoteUseCase
import com.funyaani.todolist.presentation.utils.ToDoEvent
import com.funyaani.todolist.presentation.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val noteUseCase: NoteUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ToDoState())
    val state: StateFlow<ToDoState> = _state

    private var deletedTodo: Todo? = null
    var searchQuery = MutableStateFlow("")

    init {
        loadTodos()
        startTimerForOverdueCheck()
    }

    private fun loadTodos(category: Category? = null) {
        viewModelScope.launch {
            noteUseCase.getTodosUseCase.execute(category).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true,
                            errorMessage = null,
                        )
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            todoGroups = result.data ?: emptyMap(),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun startTimerForOverdueCheck() {
        viewModelScope.launch {
            while (true) {
                val nowDate = LocalDate.now()
                val nowTime = LocalTime.now()
                var hasOverdueUpdate = false

                _state.value.todoGroups.forEach { (_, todos) ->
                    todos.forEach { todo ->
                        if (isTodoOverdue(todo, nowDate, nowTime)) {
                            updateTodoToOverdue(todo)
                            hasOverdueUpdate = true
                        } else if (todo.date.isEqual(nowDate) && todo.time.isAfter(nowTime)) {
                            scheduleNotification(todo)
                        }
                    }
                }
                if (hasOverdueUpdate) {
                    loadTodos()
                }

                delay(20000)
            }
        }
    }

    private fun isTodoOverdue(todo: Todo, nowDate: LocalDate, nowTime: LocalTime): Boolean {
        return todo.date.isBefore(nowDate) || (todo.date.isEqual(nowDate) && todo.time.isBefore(nowTime))
    }

    /**
     * Update the todo to indicate it is overdue and schedule a notification.
     */
    private fun updateTodoToOverdue(todo: Todo) {
        val updatedTodo = todo.copy(previousCategory = todo.category)
        viewModelScope.launch {
            noteUseCase.addTodoUseCase(updatedTodo)
            // No need to reload todos here; startTimerForOverdueCheck will handle it
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(todo: Todo) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", todo.title)
            putExtra("description", "Don't forget to do this work")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmTime = ZonedDateTime.of(todo.date, todo.time, ZoneId.systemDefault()).toInstant().toEpochMilli()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )
    }

    private fun searchTodos(query: String) {
        viewModelScope.launch {
            try {
                val searchResults = noteUseCase.searchTodo(query)
                _state.value = _state.value.copy(
                    todoGroups = mapOf("Search Results" to searchResults),
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun onEvent(event: ToDoEvent) {
        when (event) {
            is ToDoEvent.DeleteToDoItem -> {
                viewModelScope.launch {
                    deletedTodo = event.todo
                    noteUseCase.deleteTodoUseCase(event.todo.id)
                    sendUiEvent(UiEvent.ShowSnackbar(
                        message = "Todo Deleted",
                        action = "Undo"
                    ))
                    loadTodos()
                }
            }
            is ToDoEvent.OnUndoDeleteClick -> {
                deletedTodo?.let {
                    viewModelScope.launch {
                        noteUseCase.addTodoUseCase(it)
                        deletedTodo = null
                        loadTodos()
                    }
                }
            }
            is ToDoEvent.OnCheck -> {
                viewModelScope.launch {
                    noteUseCase.addTodoUseCase(
                        event.todo.copy(
                            category = Category.FINISHED,
                            isCompleted = true
                        )
                    )
                }
            }
            is ToDoEvent.OnUnCheck -> {
                viewModelScope.launch {
                    noteUseCase.addTodoUseCase(
                        event.todo.copy(
                            category = event.todo.previousCategory,
                            isCompleted = false
                        )
                    )
                }
            }
            is ToDoEvent.SearchTodos -> {
                searchTodos(event.query)
            }
            else -> {}
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}
