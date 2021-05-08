package com.example.ess.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class FeedItem(
        val title: String,
        val description: String?,
        val downloadUrl: String?,
        val fileName: String?,
        val publishedBy: String,
        val publisherUid: String?,
        val publisherImageUrl: String?,
        val deadline: String,
        val commentsCount: String,
        val subscriptionsCount: String
):Parcelable
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
}