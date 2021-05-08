package com.example.ess.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
        val chatId: String,
        val imageUrl: String,
        val lastMessage: String,
        val myUid: String,
        val name: String,
        val receiverUid: String
):Parcelable
class ContactMapper{
    companion object{
        fun modelToMap(contact: Contact):Map<String,Any>{
            return mapOf(
                    "name" to contact.name,
                    "image_url" to contact.imageUrl,
                    "last_message" to contact.lastMessage,
                    "my_uid" to contact.myUid,
                    "receiver_uid" to contact.receiverUid,
                    "chat_id" to contact.chatId
            )
        }

        fun mapToModel(map: HashMap<String,*>): Contact{
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