package com.example.ess.model

import com.google.firebase.database.PropertyName

data class ActivityItem(
        @get:PropertyName("class_name")
        @set:PropertyName("class_name")
        var className: String = "",
        @get:PropertyName("issue_name")
        @set:PropertyName("issue_name")
        var issueName: String = "",
        val job: String = "",
        val timestamp: String = "",
        val type: String = ""

)