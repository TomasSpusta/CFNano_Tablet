package com.nano_tablet.nanotabletrfid.mainApp.domain.schemas

import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Createdby
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.GeDefaultprojectid
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.GeInstitution1
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.GePrimaryrgid
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.GeSystemuserid
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.GeWorkplace1
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Modifiedby
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.NewGarantkontakt
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Parentcustomerid

data class ContactSchema(
    val address1_city: String,
    val address1_line1: String,
    val address1_line2: String,
    val address1_line3: String,
    val createdby: Createdby,
    val createdon: String,
    val emailaddress1: String,
    val entityname: String,
    val firstname: String,
    val ge_defaultprojectid: GeDefaultprojectid,
    val ge_fullname_noncz: String,
    val ge_institution1: GeInstitution1,
    val ge_is_employee: Boolean,
    val ge_persid1: String,
    val ge_primaryrgid: GePrimaryrgid,
    val ge_synchronise: Boolean,
    val ge_systemuserid: GeSystemuserid,
    val ge_tipred: String,
    val ge_tiza: String,
    val ge_workplace1: GeWorkplace1,
    val id: String,
    val lastname: String,
    val mobilephone: String,
    val modifiedby: Modifiedby,
    val modifiedon: String,
    val new_garantkontakt: NewGarantkontakt,
    val new_name_noncz2: String,
    val new_odhlaseniodberuzpravpt: Boolean,
    val new_odkazwebceitec: String,
    val new_osobniemailuzivatele: String,
    val new_osobnitelefonuzivatele: String,
    val new_rfid: String,
    val parentcustomerid: Parentcustomerid,
    val statecode: Int,
    val statuscode: Int,
    val telephone1: String
)

