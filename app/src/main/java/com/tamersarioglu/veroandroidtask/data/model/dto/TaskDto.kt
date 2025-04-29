package com.tamersarioglu.veroandroidtask.data.model.dto

import com.google.gson.annotations.SerializedName

data class TaskDto(
    @SerializedName("task")
    val task: String,

    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("sort")
    val sort: String?, // Kept as String as it appears as "0"

    @SerializedName("wageType")
    val wageType: String?,

    @SerializedName("BusinessUnitKey")
    val businessUnitKey: String?,

    @SerializedName("businessUnit")
    val businessUnit: String?,

    @SerializedName("parentTaskID")
    val parentTaskID: String?,

    @SerializedName("preplanningBoardQuickSelect")
    val preplanningBoardQuickSelect: String?, // Type inferred as String? due to null

    @SerializedName("colorCode")
    val colorCode: String?,

    @SerializedName("workingTime")
    val workingTime: String?, // Type inferred as String? due to null

    @SerializedName("isAvailableInTimeTrackingKioskMode")
    val isAvailableInTimeTrackingKioskMode: Boolean,

    @SerializedName("isAbstract")
    val isAbstract: Boolean
)