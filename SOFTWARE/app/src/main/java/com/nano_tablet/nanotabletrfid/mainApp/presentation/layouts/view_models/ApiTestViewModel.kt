package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.data.api_interfaces.MyApi
import com.nano_tablet.nanotabletrfid.mainApp.data.error_mapper.toNetworkError
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.NetworkError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject
import kotlin.jvm.java

@HiltViewModel
class ErrorTestViewModel @Inject constructor() : ViewModel() {

    private val _errorFlow = MutableStateFlow<NetworkError?>(null)
    val errorFlow: StateFlow<NetworkError?> = _errorFlow.asStateFlow()

    private var mockWebServer: MockWebServer? = null
    private var api: MyApi? = null

    fun simulateApiError(statusCode: Int, message: String = "Simulated error") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //mockWebServer?.shutdown()
            } catch (_: Exception) {
            }

            try {
                mockWebServer = MockWebServer().apply {
                    start()

                    enqueue(
                        MockResponse()
                            .setResponseCode(statusCode)
                            .setBody(message)
                    )
                }
            } catch (_: Exception) {
            }
            val retrofit = Retrofit.Builder()
                .baseUrl(mockWebServer!!.url("/"))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            api = retrofit.create(MyApi::class.java)

            makeApiCall()
        }
    }


    fun makeApiCall() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api?.getTest()
                Log.d("API", "Response: $response")
                if (response?.isSuccessful == true ) {
                    Log.d("API", "Success: ${response.body()}")
                } else {
                    throw HttpException(response!!)
                }
            } catch (t: Throwable) {
                Log.e("API", "Caught exception: ${t::class.simpleName} - ${t.message}")
                _errorFlow.value = t.toNetworkError()
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        try {
            mockWebServer?.shutdown()
        } catch (_: Exception) {
        }
    }
}