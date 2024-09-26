package com.funyaani.todolist.presentation.viewmodel

import com.funyaani.todolist.domain.Category
import com.funyaani.todolist.domain.Todo

data class ToDoState(
    val todoGroups: Map<String, List<Todo>> = emptyMap(),
    val searchResults: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSearching: Boolean = false,
    val selectedCategory: Category? = null

)
