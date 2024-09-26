package com.funyaani.todolist.domain.UseCase

import com.funyaani.todolist.common.Resource
import com.funyaani.todolist.domain.Category
import com.funyaani.todolist.domain.Todo
import com.funyaani.todolist.domain.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject


class GetTodosUseCase (
    private val repository: TodoRepository
) {

    fun execute(category: Category? = null): Flow<Resource<Map<String, List<Todo>>>> = flow {
        try {
            emit(Resource.Loading())

            val todosFlow = if (category != null) {
                repository.getTodos(category)
            } else {
                repository.getAllTodo()
            }

            val nowDate = LocalDate.now()
            val nowTime = LocalTime.now()

            val groupedTodosFlow = todosFlow.map { todos ->
                val groupedTodos = mutableMapOf<String, List<Todo>>()

                val overdueTodos = todos.filter {
                    (it.date.isBefore(nowDate) ||
                            (it.date.isEqual(nowDate) && it.time.isBefore(nowTime))) && !it.isCompleted
                }
                val todayTodos = todos.filter { it.date.isEqual(nowDate) && it.time.isAfter(nowTime) }
                val tomorrowTodos = todos.filter { it.date.isEqual(nowDate.plusDays(1)) }
                val thisWeekTodos = todos.filter {
                    it.date.isAfter(nowDate.plusDays(1)) &&
                            it.date.isBefore(nowDate.plusWeeks(1))
                }
                val nextWeekTodos = todos.filter {
                    it.date.isAfter(nowDate.plusWeeks(1)) &&
                            it.date.isBefore(nowDate.plusWeeks(2))
                }
                val nextMonthTodos = todos.filter {
                    it.date.isAfter(nowDate.plusWeeks(2)) &&
                            it.date.isBefore(nowDate.plusMonths(1))
                }
                val laterTodos = todos.filter { it.date.isAfter(nowDate.plusMonths(1)) }

                groupedTodos["Overdue"] = overdueTodos
                groupedTodos["Today"] = todayTodos
                groupedTodos["Tomorrow"] = tomorrowTodos
                groupedTodos["This Week"] = thisWeekTodos
                groupedTodos["Next Week"] = nextWeekTodos
                groupedTodos["Next Month"] = nextMonthTodos
                groupedTodos["Later"] = laterTodos

                // Debugging logs
                println("Overdue Todos: ${overdueTodos.size}")
                println("Today Todos: ${todayTodos.size}")
                // Add logs for other categories as well

                groupedTodos
            }

            emitAll(groupedTodosFlow.map { Resource.Success(it) })

        } catch (e: Exception) {
            emit(Resource.Error("Failed to load todos: ${e.message}"))
        }
    }
}


