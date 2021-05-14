package com.example.ess.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
@IgnoreExtraProperties
data class Comments(
        var comments: HashMap<String,Comment> = hashMapOf()
)
@IgnoreExtraProperties
data class Comment(
        val path: String = "",
        val comment: String = "",
        @get:PropertyName("image_url")
        @set:PropertyName("image_url")
        var imageUrl: String = "",
        val name: String = "",
        val uid: String = "",
        val timestamp: String = "",
        /*@get:PropertyName("comment_answers")
        @set:PropertyName("comment_answers")
        var subComments:HashMap<String,SubComments> = hashMapOf(),
        var subCommentsCount: String = ""*/
)
data class SubComments(
        val comment: String = "",
        @get:PropertyName("image_url")
        @set:PropertyName("image_url")
        var imageUrl: String = "",
        val name: String = "",
        val uid: String = "",
        val timestamp: String = ""
)
