package com.nano_tablet.nanotabletrfid.mainApp.domain.schemas

import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Createdby
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.GeProjectsid
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Modifiedby


data class SampleSchema(
    val createdby: Createdby,
    val createdon: String,
    val entityname: String,
    val ge_description: String,
    val ge_location: String,
    val ge_name: String,
    val ge_projectsid: GeProjectsid,
    val id: String,
    val modifiedby: Modifiedby,
    val modifiedon: String,
    val new_code: String,
    val statecode: Int,
    val statuscode: Int
)

