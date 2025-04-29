package com.tamersarioglu.veroandroidtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tamersarioglu.veroandroidtask.presentation.login.LoginScreen
import com.tamersarioglu.veroandroidtask.presentation.tasklist.TaskListScreen
import com.tamersarioglu.veroandroidtask.presentation.ui.theme.VeroAndroidTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VeroAndroidTaskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("tasklist") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("tasklist") {
                            TaskListScreen(
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("tasklist") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}