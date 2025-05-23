package com.tamersarioglu.veroandroidtask.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tamersarioglu.veroandroidtask.presentation.auth.AuthState
import com.tamersarioglu.veroandroidtask.presentation.auth.AuthViewModel
import com.tamersarioglu.veroandroidtask.presentation.login.LoginScreen
import com.tamersarioglu.veroandroidtask.presentation.tasklist.TaskListScreen

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        is AuthState.Authenticated -> {
            LaunchedEffect(authState) {
                navController.navigate("tasklist") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
        is AuthState.Unauthenticated -> {
            LaunchedEffect(authState) {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authState is AuthState.Authenticated) "tasklist" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    authViewModel.onLoginSuccess()
                }
            )
        }

        composable("tasklist") {
            TaskListScreen(
                onLogout = {
                    authViewModel.onLogout()
                }
            )
        }
    }
}