package hr.foi.rampu.stanarko.entities

data class Rent(
    val id: Int,
    val tenant: Tenant?,
    val month_to_be_paid: Int,
    val year_to_be_paid: Int,
    val rent_paid: Boolean = false
){
    constructor() : this(0, null, 0, 0,false)
}
