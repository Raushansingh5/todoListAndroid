package com.funyaani.todolist.presentation.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.funyaani.todolist.domain.Category
import com.funyaani.todolist.presentation.utils.TaskEvent
import com.funyaani.todolist.presentation.utils.UiEvent
import com.funyaani.todolist.presentation.viewmodel.ToDoEditViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoEditScreen(
    navController: NavController,
    viewModel: ToDoEditViewModel = hiltViewModel(),
    todoId: String? = null
) {
    val state by viewModel.state.collectAsState()
    val scaffoldState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Remember states for opening date and time pickers
    val openDatePicker = remember { mutableStateOf(false) }
    val openTimePicker = remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                   scaffoldState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }
                is UiEvent.SaveEvent -> {
                    navController.navigateUp()
                }
            }
        }
    }

    // DatePickerDialog
    if (openDatePicker.value) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                viewModel.onEvent(TaskEvent.DueDateChanged(selectedDate))
                openDatePicker.value = false
            },
            LocalDate.now().year,
            LocalDate.now().monthValue - 1,
            LocalDate.now().dayOfMonth
        ).show()
    }

    // TimePickerDialog
    if (openTimePicker.value) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val selectedTime = LocalTime.of(hourOfDay, minute)
                viewModel.onEvent(TaskEvent.DueTimeChanged(selectedTime))
                openTimePicker.value = false
            },
            LocalTime.now().hour,
            LocalTime.now().minute,
            false // is24HourView = false for 12-hour format
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (todoId != null) "Edit Task" else "New Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF033E64), // Background color
                    titleContentColor = Color.White,  // Title color
                    actionIconContentColor = Color.White // Icons color
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(TaskEvent.SaveTask) },
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFF033E64)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save Task")
            }
        },
        snackbarHost = { SnackbarHost(hostState = scaffoldState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF001D36))
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Task Title Input
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.onEvent(TaskEvent.TitleChanged(it)) },
                    label = { Text("What is to be done?") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openDatePicker.value = true }
                ) {
                    Text(
                        text = "Due date: ${state.date?.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")) ?: "Select a date"}",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Select Date")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Time Picker Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openTimePicker.value = true }
                ) {
                    Text(
                        text = "Time: ${state.time?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Select a time"}",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Select Time")
                }

                Spacer(modifier = Modifier.height(16.dp))


                // Category Dropdown

                Box(modifier = Modifier.fillMaxWidth()
                    .wrapContentSize(Alignment.TopEnd)) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = state.category.name,
                            onValueChange = {},
                            label = { Text(text = "Todo Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            Category.values()
                                .filter { it != Category.FINISHED } // Exclude the "finished" option
                                .forEach { option: Category ->
                                DropdownMenuItem(
                                    text = { Text(text = option.name) },
                                    onClick = {
                                        expanded = false
                                        viewModel.onEvent(TaskEvent.CategoryChanged(option))
                                    }
                                )
                            }
                        }
                        }
                    }
                Spacer(modifier = Modifier.height(16.dp))

                state.errorMessage?.let {
                    Text(text = "Error: $it", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNewTaskScreen() {
    // Example preview usage, no need for navController in preview
    ToDoEditScreen(navController = NavController(LocalContext.current))
}
