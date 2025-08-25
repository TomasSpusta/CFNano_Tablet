package com.nano_tablet.nanotabletrfid.mainApp.domain.models

data class NetworkError(
    val error: ApiErrorDetail,
    val t: Throwable? = null
)

enum class ApiError(val defaultMessage: String) {
    NetworkError("NetworkError"),
    UnknownResponse("Unknown Response"),
    UnknownError("Unknown Error"),

    BadRequest("You are probably not authorized to operate the machine, reservation not created"),
    Unauthorized("Unauthorized - Old Token"),
    NotFound("Not Found");

    fun withMessage(message: String): ApiErrorDetail {
        return ApiErrorDetail(
            code = this,
            message = message.ifEmpty{defaultMessage}
        )
    }
}

