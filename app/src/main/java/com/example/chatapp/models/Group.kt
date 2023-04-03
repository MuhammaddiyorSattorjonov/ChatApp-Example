package com.example.chatapp.models

class Group:java.io.Serializable {
    var id:String? = null
    var name:String? = null
    var groupUsersId:String? = null
    var listMessages:String? = null
    var image:String? = null

    constructor()
    constructor(
        id: String?,
        groupUsersIds: String?,
        name: String?,
        listMessages: String?,
        groupMemberCount: String?,
    ) {
        this.id = id
        this.groupUsersId = groupUsersIds
        this.name = name
        this.listMessages = listMessages
    }

}