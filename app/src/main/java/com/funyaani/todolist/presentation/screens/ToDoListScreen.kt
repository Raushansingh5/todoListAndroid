package com.funyaani.todolist.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.funyaani.todolist.domain.Todo
import com.funyaani.todolist.presentation.utils.Routes
import com.funyaani.todolist.presentation.utils.ToDoEvent
import com.funyaani.todolist.presentation.utils.UiEvent
import com.funyaani.todolist.presentation.viewmodel.ToDoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter



@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoListScreen(
    viewModel: ToDoViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val scaffoldState = remember { SnackbarHostState() }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val result = scaffoldState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(ToDoEvent.OnUndoDeleteClick)
                    }
                }
                is UiEvent.SaveEvent -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChanged = { query ->
                                viewModel.onEvent(ToDoEvent.SearchTodos(query))
                                viewModel.searchQuery.value = query // Update the search query state
                            },
                            onCloseSearch = { isSearching = false }
                        )
                    } else {
                        Text("All Lists")
                    }
                },
                actions = {
                    if (!isSearching) {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF033E64),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.TODO_EDIT) },
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFF033E64)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        snackbarHost = { SnackbarHost(hostState = scaffoldState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF001D36))
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage ?: "Unknown error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                state.todoGroups.isEmpty() || state.todoGroups.values.all { it.isEmpty() } -> {
                    Text(
                        text = "No Data",
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 20.dp)
                    )
                }
                else -> {
                    LazyColumn {
                        state.todoGroups.forEach { (header, todos) ->
                            if (todos.isNotEmpty()) {
                                item {
                                    Text(
                                        text = header,
                                        color = when (header) {
                                            "Overdue" -> Color.Red
                                            else -> Color.White
                                        },
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                items(todos) { todo ->
                                    TodoItem(
                                        todo = todo,
                                        modifier = Modifier
                                            .clickable {
                                                navController.navigate(
                                                    Routes.TODO_EDIT + "?todoId=${todo.id}"
                                                )
                                            }
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .background(Color(0xFF002D62), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        onCheck = {
                                            viewModel.onEvent(ToDoEvent.OnCheck(todo))
                                        },
                                        onUnCheck = {
                                            viewModel.onEvent(ToDoEvent.OnUnCheck(todo))
                                        },
                                        onDelete = {
                                            viewModel.onEvent(ToDoEvent.DeleteToDoItem(todo))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        placeholder = { Text("Search...") },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        trailingIcon = {
            IconButton(onClick = onCloseSearch) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Search")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}

@Composable
fun TodoItem(
    todo: Todo,
    modifier: Modifier = Modifier,
    onCheck: () -> Unit,
    onUnCheck: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = {
                if (it) onCheck() else onUnCheck()
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = todo.title, color = Color.White)
            Text(
                text = "${todo.date} ${todo.time}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
        }
    }
}


@Preview
@Composable
fun PreviewToDoListScreen() {
    ToDoListScreen(navController = rememberNavController())
}
