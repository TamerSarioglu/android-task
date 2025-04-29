package com.tamersarioglu.veroandroidtask.data.model.dto

data class AuthResponse(
    val oauth: OAuthData,
    val userInfo: UserInfo,
    val permissions: List<String>,
    val apiVersion: String,
    val showPasswordPrompt: Boolean
)

data class OAuthData(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val scope: String?,
    val refresh_token: String
)

data class UserInfo(
    val personalNo: Int,
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val active: Boolean,
    val businessUnit: String
)