package com.nano_tablet.nanotabletrfid.di

import com.nano_tablet.nanotabletrfid.mainApp.data.repository.ContactRepositoryImpl
import com.nano_tablet.nanotabletrfid.mainApp.data.repository.LogRepositoryImpl
import com.nano_tablet.nanotabletrfid.mainApp.data.repository.OperationsRepositoryImpl
import com.nano_tablet.nanotabletrfid.mainApp.data.repository.ProjectsRepositoryImpl
import com.nano_tablet.nanotabletrfid.mainApp.data.repository.ReservationRepositoryImpl
import com.nano_tablet.nanotabletrfid.mainApp.data.repository.MyReservationsRepositoryImpl

import com.nano_tablet.nanotabletrfid.mainApp.data.repository.SamplesRepositoryImpl
import com.nano_tablet.nanotabletrfid.mainApp.data.repository.TokenRepositoryImpl
import com.nano_tablet.nanotabletrfid.mainApp.data.repository.UserRepositoryImpl
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.ContactRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.LogRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.OperationsRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.ProjectsRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.ReservationRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.MyReservationsRepository

import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.SamplesRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.TokenRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds repository interfaces to their concrete implementations.
 *
 * Using `@Binds` instead of `@Provides` lets Hilt know which implementation
 * should be injected whenever an interface is requested.
 *
 * All bindings are scoped to [SingletonComponent], meaning a single instance
 * of each repository will be used throughout the application.
 *
 * Example:
 * ```
 * // Somewhere in a ViewModel:
 * class UserViewModel @Inject constructor(
 *     private val userRepository: UserRepository
 * ) : ViewModel() {
 *     // Hilt will automatically inject UserRepositoryImpl
 * }
 * ```
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindOperationsRepository(impl: OperationsRepositoryImpl): OperationsRepository


    @Binds
    @Singleton
    abstract fun bindTokenRepository(impl: TokenRepositoryImpl): TokenRepository

    @Binds
    @Singleton
    abstract fun bindProjectsRepository(impl: ProjectsRepositoryImpl): ProjectsRepository

    @Binds
    @Singleton
    abstract fun bindSamplesRepository(impl: SamplesRepositoryImpl): SamplesRepository

    @Binds
    @Singleton
    abstract fun bindReservationRepository(impl: ReservationRepositoryImpl): ReservationRepository

    @Binds
    @Singleton
    abstract fun bindLogRepository(impl: LogRepositoryImpl): LogRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(impl: ContactRepositoryImpl): ContactRepository

    @Binds
    @Singleton
    abstract fun bindReservationsRepository(impl: MyReservationsRepositoryImpl): MyReservationsRepository
}