package com.example.ess.model

import java.io.FileDescriptor
import java.sql.Timestamp

data class Notification(
    var priority: Int,
    var title: String,
    var descripton: String,
    var timestamp: Long
)