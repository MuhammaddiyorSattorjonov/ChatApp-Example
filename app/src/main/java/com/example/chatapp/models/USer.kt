package com.example.chatapp.models

class User:java.io.Serializable {
    var uid:String? = null
    var name:String? = null
    var imageLink:String? = null

    constructor(uid: String?, name: String?, imageLink: String?) {
        this.uid = uid
        this.name = name
        this.imageLink = imageLink
    }

    constructor()

}