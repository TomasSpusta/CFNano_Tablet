package com.nano_tablet.nanotabletrfid.mainApp.data.error_mapper

import com.google.gson.Gson
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.ApiError
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.ApiErrorBody
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.NetworkError
import retrofit2.HttpException
import java.io.IOException

/**
 * Converts any [Throwable] encountered during a network call into a [NetworkError].
 *
 * This centralizes error mapping so repositories can handle a uniform type
 * instead of dealing with raw exceptions.
 *
 * Mapping rules:
 * - [IOException] → treated as network unavailability ([ApiError.NetworkError]).
 * - [HttpException] → attempts to parse error body JSON into [ApiErrorBody];
 *   if parsing succeeds, uses its `code` and `message`.
 *   Otherwise, falls back to `"Unknown error (code X)"`.
 *   Then maps HTTP codes:
 *     - 400 → [ApiError.BadRequest]
 *     - 401 → [ApiError.Unauthorized]
 *     - 404 → [ApiError.NotFound]
 *     - else → [ApiError.UnknownResponse]
 * - Any other exception → [ApiError.UnknownError].
 *
 * @receiver Throwable from Retrofit/OkHttp/repositories.
 * @return [NetworkError] containing mapped [ApiErrorDetail] and original throwable.
 */
fun Throwable.toNetworkError(): NetworkError {
    val error = when (this) {
        is IOException -> ApiError.NetworkError.withMessage("Network unavailable")

        // HTTP response with non-2xx status
        is HttpException -> {
            val rawBody = this.response()?.errorBody()?.string().orEmpty()
            // Try to parse JSON error body into a known schema
            val parsedError = try {
                Gson().fromJson(rawBody, ApiErrorBody::class.java)
            } catch (e: Exception) {
                null
            }
            val message = parsedError?.let {
                "Error ${it.code}: ${it.message}"
            } ?: "Unknown error (code ${this.code()})"

            when (this.code()) {
                400 -> ApiError.BadRequest.withMessage(message)
                401 -> ApiError.Unauthorized.withMessage(message)
                404 -> ApiError.NotFound.withMessage(message)
                else -> ApiError.UnknownResponse.withMessage(message)
            }
        }

        else -> ApiError.UnknownError.withMessage(this.message ?: "Unknown error")
    }
    return NetworkError(
        error = error,
        t = this
    )
}