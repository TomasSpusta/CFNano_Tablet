package com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces

import com.nano_tablet.nanotabletrfid.mainApp.data.repository.TokenHandling
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp interceptor that attaches an `Authorization: Bearer <token>` header
 * to all outgoing requests, if a token is available.
 *
 * This allows Retrofit/OkHttp API calls to automatically include authentication
 * without each repository needing to manually add headers.
 *
 * Typical flow:
 * - [TokenHandling] loads the current token (e.g., from preferences or memory).
 * - If a non-null token exists, it is appended as an `Authorization` header.
 * - Otherwise the request proceeds without authentication.
 */
class AuthInterceptor @Inject constructor(
   private val tokenHandling: TokenHandling,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenHandling.loadToken().first
        val requestBuilder: Request.Builder = chain.request().newBuilder()
        token?.let {
            requestBuilder
                .addHeader(
                    "Authorization",
                    "Bearer $it"
                )
        }
        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }
}