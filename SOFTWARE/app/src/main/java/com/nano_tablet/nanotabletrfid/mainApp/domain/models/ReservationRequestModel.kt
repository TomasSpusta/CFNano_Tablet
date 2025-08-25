package com.nano_tablet.nanotabletrfid.mainApp.domain.models

data class ReservationRequestModel(
    val equipment: List<String>,
    val fields: Map<String, Map<String, Any>>,
    val description: String,
    val from: String,
    val project: String,
    val realised_for: String,
    val research_group: String,
    val samples: List<String>? = emptyList(),
    val status_code: Int,
    val time_requirement: Int,
    val to: String,

)