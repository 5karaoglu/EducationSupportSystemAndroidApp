package com.example.ess.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class IssueShort(
        val path: String = "",
        val title: String = "",
        val description: String = "",
        val deadline: String = "",
        @get:PropertyName("published_by")
        @set:PropertyName("published_by")
        var publishedBy: String = "",
        @get:PropertyName("file_name")
        @set:PropertyName("file_name")
        var fileName: String? = null,
        @get:PropertyName("download_url")
        @set:PropertyName("download_url")
        var downloadUrl: String? = null
)
class IssueShortMapper{
    companion object{
        fun mapToModel(map: HashMap<String,*>): IssueShort{
            return IssueShort(
                    title = map["title"] as String,
                    description = map["description"] as String,
                    deadline = map["deadline"] as String,
                    publishedBy = map["published_by"] as String,
                    fileName = map["file_name"] as String,
                    downloadUrl = map["download_url"] as String
            )
        }
        fun modelToMap(issueShort: IssueShort): Map<String, String?> {
            return mapOf(
                "title" to issueShort.title,
                "description" to issueShort.description,
                "deadline" to issueShort.deadline,
                "published_by" to issueShort.publishedBy,
                "file_name" to issueShort.fileName,
                "download_url" to issueShort.downloadUrl
            )
        }
    }
}