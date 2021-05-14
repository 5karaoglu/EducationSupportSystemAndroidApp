package com.example.ess.model

import com.google.firebase.database.PropertyName

data class Submit(
    var path: String = "",
    var uid: String = "",
    var name: String = "",
    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String = "",
    var timestamp: String = "1",
    @get:PropertyName("file_name")
    @set:PropertyName("file_name")
    var fileName: String = "",
    @get:PropertyName("download_url")
    @set:PropertyName("download_url")
    var downloadUrl: String = ""
)
