package com.funyaani.todolist.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.funyaani.todolist.presentation.screens.ToDoEditScreen
import com.funyaani.todolist.presentation.screens.ToDoListScreen
import com.funyaani.todolist.presentation.ui.theme.ToDoListTheme
import com.funyaani.todolist.presentation.utils.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            ToDoListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Routes.TODO_LIST
                    ) {
                        composable(route = Routes.TODO_LIST) {
                            ToDoListScreen(navController = navController)
                        }
                        composable(
                            route = Routes.TODO_EDIT +
                                    "?todoId={todoId}",
                            arguments = listOf(
                                navArgument(
                                    name = "todoId"
                                ) {
                                    type = NavType.LongType
                                    defaultValue = -1
                                }
                            )
                        ) {
                            ToDoEditScreen(
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ToDoListTheme {
            Greeting("Android")
        }
    }
}
