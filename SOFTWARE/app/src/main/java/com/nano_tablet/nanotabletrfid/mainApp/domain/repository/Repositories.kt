package com.nano_tablet.nanotabletrfid.mainApp.domain.repository

import arrow.core.Either

import com.nano_tablet.nanotabletrfid.mainApp.domain.models.NetworkError
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.ReservationRequestModel
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.ReservationResponse
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.SampleSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Token
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.ContactSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.OperationSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.ProjectSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.ReservationSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.UserSchema

import retrofit2.Response

interface UserRepository {
    suspend fun fetchUser(cardId: String): Either<NetworkError, List<UserSchema>>
}

interface OperationsRepository {
    suspend fun fetchOperations(selectedInstrumentId: String): Either<NetworkError, List<OperationSchema>>
}


interface TokenRepository {
    suspend fun fetchToken(): Either<NetworkError, Token>
}

interface ProjectsRepository {
    suspend fun fetchProjects(userId: String): Either<NetworkError, List<ProjectSchema>>
    suspend fun fetchDefaultProject(defaultProjectId: String): Either<NetworkError, ProjectSchema>
}

interface SamplesRepository {
    suspend fun fetchSamples(userId: String): Either<NetworkError, List<SampleSchema>>
}

interface ReservationRepository {
    suspend fun makeReservation(requestModel: ReservationRequestModel): Either<NetworkError, ReservationResponse>

}

interface LogRepository {
    suspend fun sendLog(logData: List<String>): Either<NetworkError, Response<Any>>
}


interface ContactRepository {
    suspend fun fetchContact(userId: String): Either<NetworkError, ContactSchema>

}

interface MyReservationsRepository {
    suspend fun fetchReservations(
        owner: String,
        start: String,
        end: String
    ): Either<NetworkError, List<ReservationSchema>>

    suspend fun stopReservation(reservationId: String): Either<NetworkError, Response<Any>>
}

