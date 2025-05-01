package com.tamersarioglu.veroandroidtask.utils

import com.tamersarioglu.veroandroidtask.utils.Constants.ERROR_API_FAILED
import com.tamersarioglu.veroandroidtask.utils.Constants.ERROR_API_NULL_BODY
import com.tamersarioglu.veroandroidtask.utils.Constants.ERROR_NETWORK
import com.tamersarioglu.veroandroidtask.utils.Constants.ERROR_UNEXPECTED
import retrofit2.Response
import java.io.IOException

/**
 * Wraps a suspend Retrofit API call to handle potential exceptions and response status,
 * returning a Resource object.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Resource.Success(body)
            } else {
                Resource.Error(ERROR_API_NULL_BODY)
            }
        } else {
            Resource.Error(String.format(ERROR_API_FAILED, response.code(), response.message()))
        }
    } catch (e: IOException) {
        // Network errors (no connection, timeout, etc.)
        Resource.Error(String.format(ERROR_NETWORK, e.localizedMessage))
    } catch (e: Exception) {
        // Other unexpected errors (parsing issues, etc.)
        Resource.Error(String.format(ERROR_UNEXPECTED, e.localizedMessage))
    }
} 