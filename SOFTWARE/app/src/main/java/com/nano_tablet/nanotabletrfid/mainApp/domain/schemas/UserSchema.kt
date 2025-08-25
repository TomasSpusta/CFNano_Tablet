package com.nano_tablet.nanotabletrfid.mainApp.domain.schemas

import com.google.gson.annotations.SerializedName


data class UserSchema(
    @SerializedName("@odata.etag") val odata: String,
    val contactid: String,
    val firstname: String,
    val full_name: String,
    val fullname: String,
    val fullname_non_dia: String,
    val lastname: String,
    val primary_rg: String,
    val tipred: String,
    val tiza: String
)