package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.operation_types

sealed class OperationType(
    val id: Int
) {
    object TextInput : OperationType(200000000)
    object NumberInput : OperationType(200000001)
    object CheckBox : OperationType(200000002)
    object Date : OperationType(200000003)
    object SingleChoice : OperationType(200000004)
    object UploadFile : OperationType(200000006)
    object TextArea : OperationType(200000007)
    object MultipleSelection : OperationType(200000008)

    companion object {
        fun from(id: Int): OperationType = when (id) {
            200000000 -> TextInput
            200000001 -> NumberInput
            200000002 -> CheckBox
            200000003 -> Date
            200000004 -> SingleChoice
            200000006 -> UploadFile
            200000007 -> TextArea
            200000008 -> MultipleSelection
            else -> throw IllegalArgumentException("Unknown operation type: $id")

        }
    }
}