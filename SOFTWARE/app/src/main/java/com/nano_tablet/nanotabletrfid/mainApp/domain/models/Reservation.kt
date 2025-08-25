package com.nano_tablet.nanotabletrfid.mainApp.domain.models

data class Reservation(
    val name: String,
    val instrument: String,
    val id: String,
    val start: String,
    val end: String,
)