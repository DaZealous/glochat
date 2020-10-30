package net.glochat.dev.models

data class ChatDao(
    var msg_body: String = "",
    var key: String = "",
    var seen: Boolean = false,
    var msg_type: String = "",
    var time_stamp: Long = 0,
    var from: String = "",
    var to: String = "",
    var msg_name: String = "",
    var fromUsername: String = "",
    var toUsername: String = ""
)