package com.example.ess.model

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
        @get:PropertyName("chat_id")
        @set:PropertyName("chat_id")
        var chatId: String = "",
        @get:PropertyName("image_url")
        @set:PropertyName("image_url")
        var imageUrl: String = "",
        @get:PropertyName("last_message")
        @set:PropertyName("last_message")
        var lastMessage: String = "",
        @get:PropertyName("my_uid")
        @set:PropertyName("my_uid")
        var myUid: String = "",
        val name: String = "",
        @get:PropertyName("receiver_uid")
        @set:PropertyName("receiver_uid")
        var receiverUid: String = ""
) : Parcelable

class ContactMapper {
    companion object {
        fun modelToMap(contact: Contact): Map<String, Any> {
            return mapOf(
                    "name" to contact.name,
                    "image_url" to contact.imageUrl,
                    "last_message" to contact.lastMessage,
                    "my_uid" to contact.myUid,
                    "receiver_uid" to contact.receiverUid,
                    "chat_id" to contact.chatId
            )
        }

        fun mapToModel(map: HashMap<String, *>): Contact {
            return Contact(
                    name = map["name"] as String,
                    imageUrl = map["image_url"] as String,
                    lastMessage = map["last_message"] as String,
                    myUid = map["my_uid"] as String,
                    receiverUid = map["receiver_uid"] as String,
                    chatId = map["chat_id"] as String
            )
        }
    }
}