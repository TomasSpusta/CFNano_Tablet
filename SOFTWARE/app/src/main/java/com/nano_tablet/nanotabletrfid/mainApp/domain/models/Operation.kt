package com.nano_tablet.nanotabletrfid.mainApp.domain.models

data class Operation(
    val name: String,
    val id: String,
    val type: Int,
    val operationDetails: List<OperationDetail> = emptyList(),
    val selectedOperationDetails: List<OperationDetail> = emptyList(),
    val index: Int,
    val isSelected: Boolean,
    )
data class OperationDetail (
    val name: String,
    val isSelected: Boolean,
    val index: Int
)
