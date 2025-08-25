package com.nano_tablet.nanotabletrfid.mainApp.domain.models

data class ApiErrorDetail(
    val code: ApiError,
    val message: String
)

data class ApiErrorBody(
    val code: Int,
    val message: String,
    val context: Any?,
    val exception: String
)