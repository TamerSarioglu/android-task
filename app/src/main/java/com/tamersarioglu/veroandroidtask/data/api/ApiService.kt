package com.tamersarioglu.veroandroidtask.data.api

import com.tamersarioglu.veroandroidtask.data.model.dto.AuthRequest
import com.tamersarioglu.veroandroidtask.data.model.dto.AuthResponse
import com.tamersarioglu.veroandroidtask.data.model.dto.TaskDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("index.php/login")
    suspend fun login(
        @Header("Authorization") authorization: String = "Basic QVBJX0V4cGxvcmVyOjEyMzQ1NmlzQUxhbWVQYXNz",
        @Header("Content-Type") contentType: String = "application/json",
        @Body authRequest: AuthRequest
    ): Response<AuthResponse>

    @GET("dev/index.php/v1/tasks/select")
    suspend fun getTasks(
        @Header("Authorization") bearerToken: String
    ): Response<List<TaskDto>>
}