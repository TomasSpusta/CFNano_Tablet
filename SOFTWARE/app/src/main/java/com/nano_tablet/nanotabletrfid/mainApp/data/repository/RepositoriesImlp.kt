package com.nano_tablet.nanotabletrfid.mainApp.data.repository

import arrow.core.Either
import com.nano_tablet.nanotabletrfid.mainApp.data.error_mapper.toNetworkError
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.ContactApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.LogApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.OperationsApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.ProjectsApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.ReservationApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.ReservationsApi

import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.SamplesApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.TokenApi
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.UserApi
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.NetworkError

import com.nano_tablet.nanotabletrfid.mainApp.domain.models.ReservationRequestModel
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.ReservationResponse
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.SampleSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Token
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.ContactSchema

import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.ContactRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.LogRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.OperationsRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.ProjectsRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.ReservationRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.MyReservationsRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.SamplesRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.TokenRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.UserRepository
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.OperationSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.ProjectSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.ReservationSchema
import com.nano_tablet.nanotabletrfid.user.FetchUserRequestModel
import com.nano_tablet.nanotabletrfid.user.TokenRequestModel
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.UserSchema
import com.nano_tablet.nanotabletrfid.util.Constant.API_KEY

import retrofit2.Response
import javax.inject.Inject

/**
 * Repository that looks up users by RFID/card ID.
 *
 * Wraps [UserApi] and normalizes errors to [NetworkError] using [toNetworkError].
 * Returns Arrow's `Either` where `Right` is the successful payload.
 */
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
    override suspend fun fetchUser(cardId: String): Either<NetworkError, List<UserSchema>> {
        return Either.catch {
            userApi.fetchUser(FetchUserRequestModel(cardId))
        }.mapLeft { it.toNetworkError() }
    }
}

/**
 * Repository for operation/user-field schema tied to instruments/equipment.
 */
class OperationsRepositoryImpl @Inject constructor(
    private val operationsApi: OperationsApi,
) : OperationsRepository {

    override suspend fun fetchOperations(selectedInstrumentId: String):
            Either<NetworkError, List<OperationSchema>> {
        return Either.catch {
            operationsApi.fetchOperations(instrumentId = selectedInstrumentId)
        }.mapLeft { it.toNetworkError() }
    }
}

/**
 * Repository handling authentication token retrieval.
 */
class TokenRepositoryImpl @Inject constructor(
    private val tokenApi: TokenApi
) : TokenRepository {
    override suspend fun fetchToken(): Either<NetworkError, Token> {
        return Either.catch {
            tokenApi.fetchToken(TokenRequestModel(API_KEY))
        }.mapLeft { it.toNetworkError() }
    }
}

/**
 * Repository for project listing and single-project fetch.
 */
class ProjectsRepositoryImpl @Inject constructor(
    private val projectsApi: ProjectsApi,

    ) : ProjectsRepository {
    /** Loads all projects for a given user/contact. */
    override suspend fun fetchProjects(userId: String): Either<NetworkError, List<ProjectSchema>> {
        return Either.catch {
            projectsApi.fetchProjects(userId = userId)
        }.mapLeft { it.toNetworkError() }
    }
    /** Loads a single project by its ID. Default project from booking system. */
    override suspend fun fetchDefaultProject(defaultProjectId: String): Either<NetworkError, ProjectSchema> {
        return Either.catch {
            projectsApi.fetchDefaultProject(defaultProjectId = defaultProjectId)
        }.mapLeft { it.toNetworkError() }
    }
}

/**
 * Repository for user samples.
 */
class SamplesRepositoryImpl @Inject constructor(
    private val samplesApi: SamplesApi,
) : SamplesRepository {
    override suspend fun fetchSamples(userId: String): Either<NetworkError, List<SampleSchema>> {
        return Either.catch {
            samplesApi.getSamples(userId = userId)
        }.mapLeft { it.toNetworkError() }
    }
}

/**
 * Repository for creating a single reservation.
 */
class ReservationRepositoryImpl @Inject constructor(
    private val reservationApi: ReservationApi
) : ReservationRepository {
    override suspend fun makeReservation(requestModel: ReservationRequestModel): Either<NetworkError, ReservationResponse> {
        return Either.catch {
            reservationApi.makeReservation(requestModel)
        }.mapLeft { it.toNetworkError() }
    }

}
/**
 * Repository for sending logs to a webhook-like endpoint.
 */
class LogRepositoryImpl @Inject constructor(
    private val logApi: LogApi
) : LogRepository {
    override suspend fun sendLog(logData: List<String>): Either<NetworkError, Response<Any>> {
        return Either.catch {
            logApi.sendLog(logEntry = logData)
        }.mapLeft { it.toNetworkError() }
    }
}
/**
 * Repository for contact details lookup.
 */
class ContactRepositoryImpl @Inject constructor(
    private val contactApi: ContactApi
) : ContactRepository {
    override suspend fun fetchContact(userId: String): Either<NetworkError, ContactSchema> {
        return Either.catch {
            contactApi.fetchContact(userId = userId)
        }.mapLeft { it.toNetworkError() }
    }
}
/**
 * Repository for listing/controlling reservations.
 */
class MyReservationsRepositoryImpl @Inject constructor(
    private val reservationsApi: ReservationsApi
) : MyReservationsRepository {
    override suspend fun fetchReservations(
        owner: String,
        start: String,
        end: String
    ): Either<NetworkError, List<ReservationSchema>> {
        return Either.catch {
            reservationsApi.fetchReservations(owner, start, end)
        }.mapLeft { it.toNetworkError() }
    }
    /**
     * Stops a reservation by ID, returning raw HTTP [Response].
     */
    override suspend fun stopReservation(reservationId: String): Either<NetworkError, Response<Any>> {
        return Either.catch {
            reservationsApi.stopReservation(reservationId)
        }.mapLeft { it.toNetworkError() }
    }
}

