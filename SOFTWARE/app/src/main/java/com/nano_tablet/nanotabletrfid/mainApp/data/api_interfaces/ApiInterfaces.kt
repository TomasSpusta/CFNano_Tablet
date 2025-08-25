package com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces


import com.nano_tablet.nanotabletrfid.mainApp.domain.models.ReservationRequestModel
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.ReservationResponse
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.SampleSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Token
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.ContactSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.OperationSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.ProjectSchema
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.ReservationSchema
import com.nano_tablet.nanotabletrfid.user.FetchUserRequestModel
import com.nano_tablet.nanotabletrfid.user.TokenRequestModel
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.UserSchema
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OperationsApi {
    @GET("api/equipment/{id}/userfield-definitions")
    suspend fun fetchOperations(@Path("id") instrumentId: String): List<OperationSchema>
}

interface ProjectsApi {
    @GET("api/contact/{id}/projects")
    suspend fun fetchProjects(@Path("id") userId: String): List<ProjectSchema>

    @GET("api/project/{id}")
    suspend fun fetchDefaultProject(@Path("id") defaultProjectId: String): ProjectSchema
}

interface SamplesApi {
    @GET("api/contact/{id}/samples")
    suspend fun getSamples(@Path("id") userId: String): List<SampleSchema>
}

interface ContactApi {
    @GET("api/contact/{id}")
    suspend fun fetchContact(@Path("id") userId: String): ContactSchema
}

interface UserApi {
    @POST("get-contact-by-rfid")
    suspend fun fetchUser(@Body requestModel: FetchUserRequestModel): List<UserSchema>
}

interface TokenApi {
    @POST("api/login")
    suspend fun fetchToken(@Body requestModel: TokenRequestModel): Token

}

interface ReservationApi {
    @POST("api/service-appointment/simple")
    suspend fun makeReservation(@Body requestModel: ReservationRequestModel): ReservationResponse

}

interface ReservationsApi {
    @GET("api/service-appointment")

    suspend fun fetchReservations(
        @Query("filters[]") owner: String,
        @Query("filters[]") start: String,
        @Query("filters[]") end: String,
        @Query("scope") scope: String = "activity_parties.partyid"

    ): List<ReservationSchema>

    @POST("api/service-appointment/{id}/stop")
    suspend fun stopReservation(@Path("id") reservationId: String): Response<Any>
}


interface LogApi {
    @Headers("Content-Type: application/json")
    @POST("exec")
    suspend fun sendLog(@Body logEntry: List<String>): Response<Any>
}

interface MyApi {
    @GET("/")
    suspend fun getTest(): Response<String>
}
