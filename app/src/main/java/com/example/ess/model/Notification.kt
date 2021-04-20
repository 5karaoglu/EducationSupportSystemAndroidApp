package com.example.ess.model

import java.io.FileDescriptor
import java.sql.Timestamp

data class Notification(
    var priority: Int,
    var title: String,
    var descripton: String,
    var timestamp: Long
)
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
    }
}