package hr.foi.rampu.stanarko.entities

import java.util.*

data class Channel(
    val id: String,
    val participants: List<String>,
    val dateCreated: Date,
    val messages: List<Chat>) {
    constructor(): this("", emptyList(), Date(0), emptyList())
}