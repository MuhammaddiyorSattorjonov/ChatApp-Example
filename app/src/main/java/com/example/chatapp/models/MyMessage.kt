package com.example.chatapp.models

import java.text.SimpleDateFormat
import java.util.*

class MyMessage {
    var id: String? = null
    var text: String? = null
    var date: String? = null
    var fromUid: String? = null
    var toUid: String? = null
    var imageForChat:String? = null

    constructor(
        id:String?,
         text: String?,
        fromUid: String?, toUid: String?,
        imageForChat:String?,
        date: String? = SimpleDateFormat("dd.MM.yyyy HH.mm").format(
            Date())
    ) {
        this.id = id
        this.text = text
        this.date = date
        this.fromUid = fromUid
        this.toUid = toUid
        this.imageForChat = imageForChat
    }

    constructor()

}