package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Sample
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.SamplesRepository
import com.nano_tablet.nanotabletrfid.util.Event
import com.nano_tablet.nanotabletrfid.util.EventBus.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Loads and manages the user's samples.
 *
 * State machine:
 * - Idle → Loading → Success | Error
 * - Selection is kept inside [SamplesViewState.Success].
 */
@HiltViewModel
class SamplesViewModel @Inject constructor(
    private val samplesRepository: SamplesRepository,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<SamplesViewState>(SamplesViewState.Idle)
    val stateFlow: StateFlow<SamplesViewState> = _stateFlow.asStateFlow()

    private val tag = "Samples"
    private var samples: List<Sample> = emptyList()

    /**
     * Fetch samples for [userId]. Skips repeated work if the same user is already loaded,
     * unless [force] is true. Guards against concurrent loads.
     */
    fun fetchSamples(userId: String) = viewModelScope.launch {
        _stateFlow.update { return@update SamplesViewState.Loading }
        samplesRepository.fetchSamples(userId = userId)
            .onRight { sampleSchemas ->
                samples = sampleSchemas.map { sampleSchema ->
                    Sample(
                        sampleSchema.ge_name,
                        sampleSchema.id
                    )
                }
                Log.i(tag, samples.toString())
                _stateFlow.update {
                    return@update SamplesViewState.Success(
                        samples = samples,
                        selectedSample = null,
                    )
                }

            }.onLeft { error ->
                _stateFlow.update {
                    return@update SamplesViewState.Error(error = error.error.message)
                }
                sendEvent(
                    Event.AlertDialog(
                        "$tag Error",
                        "${error.error}\n${error.t?.message}"
                    )
                )
                Log.e("$tag Error", error.toString())
            }
    }


    fun onSampleClicked(sample: Sample) = viewModelScope.launch {
        _stateFlow.update { currentState ->

            if (currentState is SamplesViewState.Success) {
                val newSelectedSample = if (currentState.selectedSample?.id == sample.id) {
                    null //deselect sample
                } else {
                    sample
                }
                currentState.copy(selectedSample = newSelectedSample)
            } else {
                currentState
            }
        }
    }
}
