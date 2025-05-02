package com.tamersarioglu.veroandroidtask.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tamersarioglu.veroandroidtask.presentation.login.LoginScreen
import com.tamersarioglu.veroandroidtask.presentation.tasklist.TaskListScreen

@Composable
fun AppNavigation() {
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