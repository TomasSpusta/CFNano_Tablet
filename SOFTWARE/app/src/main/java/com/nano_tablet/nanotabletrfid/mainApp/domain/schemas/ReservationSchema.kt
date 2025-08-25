package com.nano_tablet.nanotabletrfid.mainApp.domain.schemas

import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.ActivityParty
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Createdby
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.GeRealisedForContactid
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.GeResGroupid
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Modifiedby
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Ownerid
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.PsaProjectid
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Serviceid

data class ReservationSchema(
    val activity_parties: List<ActivityParty>,
    val createdby: Createdby,
    val createdon: String,
    val description: Any,
    val entityname: String,
    val ge_affectedrezervations: Any,
    val ge_cenacelkem_base: Int,
    val ge_privatedescription: Any,
    val ge_realised_for_contactid: GeRealisedForContactid,
    val ge_res_groupid: GeResGroupid,
    val ge_typvysledkufs: Any,
    val id: String,
    val isalldayevent: Boolean,
    val modifiedby: Modifiedby,
    val modifiedon: String,
    val new_service_ticket: Any,
    val ownerid: Ownerid,
    val psa_additionalrequestcapacity: Any,
    val psa_projectid: PsaProjectid,
    val psa_timerequirement: Int,
    val regardingobjectid: Any,
    val scheduleddurationminutes: Int,
    val scheduledend: String,
    val scheduledstart: String,
    val serviceid: Serviceid,
    val statecode: Int,
    val statuscode: Int,
    val subject: String
)