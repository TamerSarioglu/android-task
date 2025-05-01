package com.tamersarioglu.veroandroidtask.utils

object Constants {
    const val BASE_URL = "https://api.baubuddy.de/"
    const val PREFS_NAME = "vero_task_preferences"
    const val AUTH_TOKEN_KEY = "auth_token"
    const val LOG_TAG = "TaskListViewModel"
    const val LOG_TASKS_LOADED = "Tasks loaded successfully: %d tasks"
    const val LOG_ERROR_LOADING = "Error loading tasks: %s"
    const val LOG_LOADING = "Loading tasks..."
    const val LOG_TASKS_REFRESHED = "Tasks refreshed successfully"
    const val LOG_ERROR_REFRESHING = "Error refreshing tasks: %s"
    const val LOG_REFRESHING = "Refreshing tasks..."
    const val ERROR_FAILED_TO_LOAD = "Failed to load tasks: %s"

    // TaskRepositoryImpl specific
    const val LOG_TAG_REPO = "TaskRepository"
    const val AUTH_SUCCESS_LOG = "Auth successful: %s"
    const val AUTH_FAILED_TOKEN_NULL = "Authentication failed: Token is null"
    const val AUTH_FAILED_API = "Authentication failed: %d %s"
    const val AUTH_FAILED_EXCEPTION = "Authentication failed: %s"
    const val ERROR_NOT_AUTHENTICATED = "Not authenticated"
    const val ERROR_REFRESH_UNKNOWN = "Unknown error during refresh"
    const val ERROR_REFRESH_UNEXPECTED_STATE = "Unexpected Loading state during refresh"
    const val ERROR_FETCH_FAILED = "Failed to fetch tasks: %d %s"
    const val ERROR_FETCH_FAILED_NULL_BODY = "Failed to fetch tasks: Response body is null"
    const val ERROR_FETCH_NO_TASKS = "No tasks available" // Used in getTasks when network is empty
    const val ERROR_LOADING_TASKS = "Error loading tasks: %s"
    const val ERROR_NETWORK = "Network error: %s"
    const val ERROR_API_NULL_BODY = "API call successful but response body was null"
    const val ERROR_API_FAILED = "API call failed: %d %s"
    const val ERROR_UNEXPECTED = "An unexpected error occurred: %s"
}