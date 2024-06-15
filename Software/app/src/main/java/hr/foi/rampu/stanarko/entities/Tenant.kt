package hr.foi.rampu.stanarko.entities

import java.util.Date

class Tenant(
    name: String,
    surname: String,
    phoneNumber: String,
    mail: String,
    val flat: Flat?,
    val dateOfMovingIn: String?,
    val dateOfMovingOut: Date?,
    role: Role = Role.TENANT
) : Person( name, surname, phoneNumber, mail, role){
    constructor() : this( "", "", "", "", null, "", null, Role.TENANT)
        override fun toString(): String {
            return "$name $surname"
        }
}