package com.tamersarioglu.veroandroidtask.data.api

import android.content.SharedPreferences
import com.tamersarioglu.veroandroidtask.utils.Constants
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val preferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = preferences.getString(Constants.AUTH_TOKEN_KEY, null)
        val request = chain.request().newBuilder()

        if (token != null && !chain.request().url.encodedPath.contains("login")) {
            request.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(request.build())
    }
}