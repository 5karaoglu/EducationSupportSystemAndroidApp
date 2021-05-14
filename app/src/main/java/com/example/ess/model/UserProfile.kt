package com.example.ess.model

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserProfile(
        val name: String = "",
        val uid: String = "",
        val email: String = "",
        @get:PropertyName("image_url")
        @set:PropertyName("image_url")
        var imageUrl: String = "",
        var classesCount: String = "",
        var friendsCount: String = ""
):Parcelable
