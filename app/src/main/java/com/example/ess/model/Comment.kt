package com.example.ess.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
@IgnoreExtraProperties
data class Comments(
        var comments: HashMap<String,Comment> = hashMapOf()
)
@IgnoreExtraProperties
data class Comment(
        val comment: String = "",
        @get:PropertyName("image_url")
        @set:PropertyName("image_url")
        var imageUrl: String = "",
        val name: String = "",
        val uid: String = "",
        val timestamp: String = "",
        @get:PropertyName("comment_answers")
        @set:PropertyName("comment_answers")
        var subcomments: HashMap<String,Subcomments> = hashMapOf()
)
data class Subcomments(
        val comment: String = "",
        @get:PropertyName("image_url")
        @set:PropertyName("image_url")
        var imageUrl: String = "",
        val name: String = "",
        val uid: String = "",
        val timestamp: String = ""
)
