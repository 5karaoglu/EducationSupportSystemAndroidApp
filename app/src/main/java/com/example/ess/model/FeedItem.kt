package com.example.ess.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class FeedItem(
        var path: String = "",
        @get:PropertyName("class_name")
        @set:PropertyName("class_name")
        var className: String = "",
        val title: String = "",
        val description: String = "",
        @get:PropertyName("download_url")
        @set:PropertyName("download_url")
        var downloadUrl: String = "",
        @get:PropertyName("file_name")
        @set:PropertyName("file_name")
        var fileName: String = "",
        @get:PropertyName("published_by")
        @set:PropertyName("published_by")
        var publishedBy: String = "",
        @get:PropertyName("publisher_uid")
        @set:PropertyName("publisher_uid")
        var publisherUid: String = "",
        @get:PropertyName("publisher_image_url")
        @set:PropertyName("publisher_image_url")
        var publisherImageUrl: String = "",
        val deadline: String = "",
        var commentsCount: String = "",
        var submitsCount: String = ""
) : Parcelable

/*
class FeedMapper{
    companion object{

        fun mapToModel(map:HashMap<String,*>):FeedItem{
            return FeedItem(
                    title = map["title"] as String,
                    description = map["description"] as String,
                    downloadUrl = map["download_url"] as String,
                    fileName = map["file_name"] as String,
                    publishedBy = map["published_by"] as String,
                    publisherUid = map["publisher_uid"] as String,
                    publisherImageUrl = map["publisher_image_url"] as String,
                    deadline = map["deadline"] as String,
                    commentsCount = map["comment_count"] as String,
                    subscriptionsCount = map["subscription_count"] as String
            )
        }
    }
}*/
