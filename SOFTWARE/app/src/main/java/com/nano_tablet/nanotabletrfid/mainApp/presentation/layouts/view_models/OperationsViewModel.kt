package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Operation
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.OperationDetail

import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.OperationsRepository
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.operation_types.OperationType
import com.nano_tablet.nanotabletrfid.util.Event
import com.nano_tablet.nanotabletrfid.util.EventBus.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OperationsViewModel @Inject constructor(
    private val operationsRepository: OperationsRepository,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<OperationsViewState>(
        value = OperationsViewState.Idle
    )
    val stateFlow: StateFlow<OperationsViewState> = _stateFlow.asStateFlow()

    private val tag = "Operations"

    /**
     * Loads operation schemas for an instrument and maps them into UI models.
     */

    fun fetchOperations(instrumentId: String) = viewModelScope.launch {
        _stateFlow.update { return@update OperationsViewState.Loading }

        operationsRepository.fetchOperations(instrumentId)
            .onRight { operationSchemas ->

                val fetchedOperations =
                    operationSchemas.mapIndexed { operationIndex, operationSchema ->
                        val operationDetailsList = operationSchema.new_note_en
                            ?.split(",")
                            ?.mapIndexed { detailIndex, detail ->
                                OperationDetail(
                                    name = detail.trim(),
                                    index = detailIndex,
                                    isSelected = false
                                )
                            } ?: emptyList()

                        Operation(
                            name = operationSchema.new_name_en,
                            id = operationSchema.id,
                            type = operationSchema.ge_type,
                            operationDetails = operationDetailsList,
                            isSelected = false,
                            index = operationIndex
                        )
                    }
                _stateFlow.update {
                    return@update OperationsViewState.Success(
                        fetchedOperations = fetchedOperations.map { operation ->
                            operation.copy(operationDetails = prepareOperationDetails(operation))
                        },
                        selectedOperation = null,
                        selectedPairs = emptyMap(),
                        selectedPairsId = emptyMap(),
                        otherField = null,

                        )
                }
            }
            .onLeft { error ->
                _stateFlow.update {
                    return@update OperationsViewState.Error(error.error.message)
                }
                sendEvent(
                    Event.AlertDialog(
                        "$tag Error",
                        "${error.error.code.name}\n${error.error.message}"
                    )
                )
                Log.e("$tag Error", error.error.message)
            }
    }


    /**
     * Records a selection/update for the given [operation], handling the various
     * [OperationType]s (text/number/date/single/multi/etc.).
     *
     */
    fun operationDetailSelection(
        operation: Operation,
        operationDetail: OperationDetail? = null,
        userInput: String? = null,
        sharedViewModel: SharedViewModel
    ) =
        viewModelScope.launch {
            _stateFlow.update { currentState ->
                if (currentState is OperationsViewState.Success) {
                    val operationId = operation.id
                    val operationType = operation.type
                    val currentSelectedDetails =
                        currentState.selectedOperationDetailsMap[operationId] ?: emptyList()


                    // Mutable map to update selections
                    val updatedSelectionMap =
                        currentState.selectedOperationDetailsMap.toMutableMap()


                    when (OperationType.from(operation.type)) {
                        is OperationType.TextInput -> {
                            if (userInput != null) {
                                updatedSelectionMap[operationId] =
                                    listOf(userInput)
                            }
                        }

                        is OperationType.NumberInput -> {
                            if (userInput != null) {
                                updatedSelectionMap[operationId] =
                                    listOf(userInput)
                            }
                        }

                        is OperationType.CheckBox -> {
                            if (userInput != null) {
                                updatedSelectionMap[operationId] =
                                    listOf(userInput)
                            }
                        }

                        is OperationType.Date -> {
                            if (userInput != null) {
                                updatedSelectionMap[operationId] =
                                    listOf(userInput)
                            }
                        }

                        is OperationType.SingleChoice -> {
                            if (operationDetail != null) {

                                updatedSelectionMap[operationId] =
                                    listOf(operationDetail.name)
                            }
                        }

                        is OperationType.UploadFile -> {
                            if (userInput != null) {
                                updatedSelectionMap[operationId] =
                                    listOf(userInput)
                            }
                        }

                        is OperationType.TextArea -> {
                            if (userInput != null) {
                                updatedSelectionMap[operationId] =
                                    listOf(userInput)
                            }
                        }

                        is OperationType.MultipleSelection -> {
                            if (operationDetail != null) {
                                val updatedSelectedDetails =
                                    currentSelectedDetails.toMutableList().apply {
                                        if (contains(operationDetail.name)) {
                                            remove(operationDetail.name)

                                        } else {
                                            add(operationDetail.name)
                                        }
                                    }

                                if (updatedSelectedDetails.isEmpty()) {
                                    updatedSelectionMap.remove(operationId)
                                } else {
                                    updatedSelectionMap[operationId] = updatedSelectedDetails
                                }
                            }
                        }
                    }

                    updatedSelectionMap.forEach { (operationId, selectedDetails) ->
                        sharedViewModel.updateSelectedOperationDetails(
                            operationId,
                            selectedDetails.joinToString(","),
                        )
                    }

                    Log.d(tag, "Current Selection State: $updatedSelectionMap")
                    currentState.copy(
                        selectedOperationDetailsMap = updatedSelectionMap
                    )
                } else currentState
            }
        }

    private fun prepareOperationDetails(selectedOperation: Operation): List<OperationDetail> {
        val prefix: Int = 200000000
        val choiceTypes = setOf(prefix + 4, prefix + 8)

        return if (selectedOperation.type in choiceTypes) {
            selectedOperation.operationDetails.mapIndexed { index, detail ->
                OperationDetail(
                    name = detail.name.trim(),
                    index = index,
                    isSelected = false
                )
            }
        } else {
            listOf(
                OperationDetail(
                    name = "",
                    index = 0,
                    isSelected = false
                )
            )
        }
    }

    fun resetState() {
        viewModelScope.launch {
            _stateFlow.value = OperationsViewState.Idle
        }
    }

}
