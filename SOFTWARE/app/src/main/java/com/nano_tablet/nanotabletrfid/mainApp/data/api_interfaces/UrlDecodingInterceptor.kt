package com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLDecoder

class UrlDecodingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        val originalUrl = request.url.toString()
        val decodedUrl = URLDecoder.decode(originalUrl, Charsets.UTF_8.toString())
        request = request.newBuilder().url(decodedUrl).build()
        return chain.proceed(request)
    }
}