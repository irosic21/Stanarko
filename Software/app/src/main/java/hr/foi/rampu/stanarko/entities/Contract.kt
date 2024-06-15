package hr.foi.rampu.stanarko.entities

import java.util.Date

data class Contract(
    val todayDate: Date?,
    val expiringDate: Date?,
    val tenant: Tenant?
){
    constructor() : this(null, null, null)
}