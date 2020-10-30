package net.glochat.dev.models

data class ContactModel (
    val name: String = "",
    val mobileNumber: String = "",
    val img_url : String = "",
    val isRegistered : Boolean = false,
    val userId : String = "",
    val username : String = ""
)