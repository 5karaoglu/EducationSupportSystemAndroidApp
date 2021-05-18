package com.example.ess.model

import com.google.firebase.database.PropertyName
import java.io.FileDescriptor
import java.sql.Timestamp

data class Notification(
        var name: String = "",
        @get:PropertyName("class_name")
        @set:PropertyName("class_name")
        var className: String = "",
        var uid: String = "",
        @get:PropertyName("image_url")
        @set:PropertyName("image_url")
        var imageUrl: String = "",
        var title: String = "",
        var description: String = "",
        var timestamp: String = ""
)
/*
class NotificationMapper{
    companion object{
        fun modelToMap(notification: Notification): Map<String, Any> {
            return mapOf<String,Any>(
                    "priority" to notification.priority,
                    "title" to notification.title,
                    "description" to notification.descripton,
                    "timestamp" to notification.timestamp
            )
        }

        fun mapToModel(map: HashMap<String,*>): Notification {
            return Notification(
                priority = map["priority"] as Long,
                title = map["title"] as String,
                descripton = map["description"] as String,
                timestamp = map["timestamp"] as Long)
        }
    }
}*/
