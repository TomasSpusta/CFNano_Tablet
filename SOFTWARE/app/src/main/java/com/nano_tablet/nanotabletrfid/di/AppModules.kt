package com.nano_tablet.nanotabletrfid.di


import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.AuthInterceptor
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.ContactApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.LogApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.OperationsApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.ProjectsApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.ReservationApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.ReservationsApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.SamplesApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.TokenApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.UrlDecodingInterceptor
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.UserApi

import com.nano_tablet.nanotabletrfid.mainApp.data.repository.TokenHandling
import com.nano_tablet.nanotabletrfid.util.Constant.BASE_URL_BOOKING
import com.nano_tablet.nanotabletrfid.util.Constant.BASE_URL_CRM
import com.nano_tablet.nanotabletrfid.util.Constant.WEBHOOK_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides application-wide dependencies.
 *
 * This module includes Retrofit APIs, OkHttp clients, and network interceptors
 * used throughout the app. All dependencies are scoped to [SingletonComponent],
 * meaning they will live as long as the application.
 */
@InstallIn(SingletonComponent::class)
@Module

object AppModules {

    /**
     * Provides a Retrofit implementation of [ContactApi].
     *
     * @param tokenInjector OkHttp client configured with authentication & URL decoding.
     */
    @Singleton
    @Provides
    fun provideContactApi(tokenInjector: OkHttpClient): ContactApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL_BOOKING)
            .addConverterFactory(GsonConverterFactory.create())
            .client(tokenInjector)
            .build()
            .create(ContactApi::class.java)
    }



    /**
     * Provides a Retrofit implementation of [LogApi].
     *
     * This API uses a dedicated OkHttp client that logs request/response bodies
     * to assist debugging when sending data to [WEBHOOK_URL].
     */
    @Singleton
    @Provides
    fun provideLogApi(): LogApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit
            .Builder()
            .baseUrl(WEBHOOK_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(LogApi::class.java)
    }


    /**
     * Provides an [AuthInterceptor] to attach authentication tokens
     * to outgoing HTTP requests.
     */
    @Singleton
    @Provides
    fun provideAuthInterceptor(tokenHandling: TokenHandling): AuthInterceptor {
        return AuthInterceptor(tokenHandling)
    }

    /**
     * Example of providing a simple configuration flag via DI.
     * This flag can be injected where URL decoding behavior needs to be toggled.
     */
    @Provides
    @Singleton
    fun provideDecodeUrl(): Boolean {
        return true
    }

    /**
     * Provides an interceptor that decodes URLs in outgoing requests.
     */
    @Provides
    @Singleton
    fun provideUrlDecodingInterceptor(): UrlDecodingInterceptor {
        return UrlDecodingInterceptor()
    }

    /**
     * Provides a configured [OkHttpClient] for APIs that require authentication
     * and URL decoding.
     *
     * @param authInterceptor Adds authentication headers.
     * @param urlDecodingInterceptor Decodes URLs in outgoing requests.
     * @param enableLogging Enables HTTP logging when true.
     */
    @Singleton
    @Provides
    fun provideTokenInjector(
        authInterceptor: AuthInterceptor,
        urlDecodingInterceptor: UrlDecodingInterceptor,
        enableLogging: Boolean = true
    ): OkHttpClient {
        val timeout: Long = 600

        val builder = OkHttpClient
            .Builder()
            .callTimeout(timeout, TimeUnit.SECONDS)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .addInterceptor(urlDecodingInterceptor)
            .addInterceptor(authInterceptor)
        if (enableLogging) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }

    /**
     * Provides Retrofit implementations for various APIs that require
     * the shared [tokenInjector].
     */
    @Provides
    @Singleton
    fun provideOperationsApi(tokenInjector: OkHttpClient): OperationsApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL_BOOKING)
            .addConverterFactory(GsonConverterFactory.create())
            .client(tokenInjector)
            .build()
            .create(OperationsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(): UserApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL_CRM)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenApi(): TokenApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL_BOOKING)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSamplesApi(tokenInjector: OkHttpClient): SamplesApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL_BOOKING)
            .addConverterFactory(GsonConverterFactory.create())
            .client(tokenInjector)
            .build()
            .create(SamplesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProjectsApi(tokenInjector: OkHttpClient): ProjectsApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL_BOOKING)
            .addConverterFactory(GsonConverterFactory.create())
            .client(tokenInjector)
            .build()
            .create(ProjectsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReservationApi(tokenInjector: OkHttpClient): ReservationApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL_BOOKING)
            .addConverterFactory(GsonConverterFactory.create())
            .client(tokenInjector)
            .build()
            .create(ReservationApi::class.java)
    }


    @Provides
    @Singleton
    fun provideReservationsApi(tokenInjector: OkHttpClient): ReservationsApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL_BOOKING)

            .addConverterFactory(GsonConverterFactory.create())
            .client(tokenInjector)
            .build()
            .create(ReservationsApi::class.java)
    }
}
