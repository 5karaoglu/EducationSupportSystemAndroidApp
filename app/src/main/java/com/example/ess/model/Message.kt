package com.example.ess.model

data class Message(
        var timestamp: String,
        var message: String,
        var whoSent: String
)

class MessageMapper {
    companion object {
        fun modelToMap(message: Message): Map<String, Any> {
            return mapOf(
                    "timestamp" to message.timestamp,
                    "message" to message.message,
                    "who_sent" to message.whoSent
            )
        }

        fun mapToModel(map: HashMap<String, *>): Message {
            return Message(
                    timestamp = map["timestamp"] as String,
                    message = map["message"] as String,
                    whoSent = map["who_sent"] as String
            )
        }
    }
}
