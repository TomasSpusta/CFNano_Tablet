package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor(

) : ViewModel() {
    private val _stateFlow = MutableStateFlow<TimeViewState>(
        TimeViewState.Set(15, null, null)
    )
    val stateFlow: StateFlow<TimeViewState> = _stateFlow

    init {
        //Log.i("timeViewModel", "init")
        //prepareTime()
    }

    fun addTime() = viewModelScope.launch {
        _stateFlow.update { currentState ->
            when (currentState) {
                is TimeViewState.Set -> {
                    TimeViewState.Set(
                        selectedTime = currentState.selectedTime + 5,
                        startTime = currentState.startTime,
                        endTime = currentState.endTime
                    )
                }

                else -> {
                    currentState
                }
            }
        }
    }


    fun subtractTime() = viewModelScope.launch {
        _stateFlow.update { currentState ->
            when (currentState) {
                is TimeViewState.Set -> {
                    val newSelectedTime = maxOf(5, currentState.selectedTime - 5)
                    TimeViewState.Set(
                        selectedTime = newSelectedTime,
                        startTime = currentState.startTime,
                        endTime = currentState.endTime
                    )
                }

                else -> {
                    currentState
                }
            }
        }
    }

    fun saveTime(selectedTime:Int) = viewModelScope.launch {
        _stateFlow.update { currentState ->
            when (currentState) {
                is TimeViewState.Set -> {
                    TimeViewState.Set(
                    selectedTime = selectedTime,
                    startTime = currentState.startTime,
                    endTime = currentState.endTime)
                }
            }
        }
    }

    fun prepareTime() =
        viewModelScope.launch {
            _stateFlow.update { currentState ->
                when (currentState) {
                    is TimeViewState.Set -> {
                        val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val startTime = LocalDateTime.now().plusMinutes(2).format(timeFormat)
                        val endTime =
                            LocalDateTime.now().plusMinutes(currentState.selectedTime.toLong())
                                .format(timeFormat)
                        //sendEvent(Event.Toast("TimePrepared, starts at ${startTime}"))
                        Log.d("Time", "TimePrepared, starts at ${currentState.selectedTime}")
                        TimeViewState.Set(
                            selectedTime = currentState.selectedTime,
                            startTime = startTime,
                            endTime = endTime
                        )
                    }
                }
            }
        }

}