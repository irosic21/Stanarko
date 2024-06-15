package hr.foi.rampu.stanarko.entities

import java.util.*

data class Chat(
    var username: String = "",
    var message: String = "",
    var timestamp: Date = Date()
)