package com.nano_tablet.nanotabletrfid.mainApp.domain.schemas

import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Createdby
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.GeEquipmentid
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.Modifiedby
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.NewEquipmentUserdefinition
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.NewFormulovslubaid
import com.nano_tablet.nanotabletrfid.mainApp.domain.schemas.CommonSchemas.NewIdVzorek

//class Operations : ArrayList<OperationSchema>()
data class OperationSchema(
    val createdby: Createdby,
    val createdon: String,
    val entityname: String,
    val ge_default_value: String,
    val ge_equipmentid: GeEquipmentid,
    val ge_name: String,
    val ge_order: Int,
    val ge_type: Int,
    val id: String,
    val modifiedby: Modifiedby,
    val modifiedon: String,
    val name: String,
    val new_description: String,
    val new_description_en: String,
    val new_editace: Boolean,
    val new_equipment_userdefinition: NewEquipmentUserdefinition,
    val new_formulovslubaid: NewFormulovslubaid,
    val new_grid: Boolean,
    val new_hidden: Boolean,
    val new_id_vzorek: NewIdVzorek,
    val new_mandatory: Boolean,
    val new_name_en: String,
    val new_note: String,
    val new_note_en: String?,
    val statecode: Int,
    val statuscode: Int
)









