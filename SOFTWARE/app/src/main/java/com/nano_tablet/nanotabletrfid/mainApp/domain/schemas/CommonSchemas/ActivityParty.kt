package com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas

data class ActivityParty(
    val activityid: Activityid,
    val entityname: String,
    val id: String,
    val ownerid: Ownerid,
    val participationtypemask: Int,
    val partyid: Partyid
)