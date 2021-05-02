package com.example.ess.model

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class IssueShort(
    val title: String,
    val description: String,
    val deadline: String,
    val published_by: String,
    val fileName: String? = null,
    val downloadUrl: String? = null
)
class IssueShortMapper{
    companion object{
        fun mapToModel(map: HashMap<String,*>): IssueShort{
            return IssueShort(
                title = map["title"] as String,
                description = map["description"] as String,
                deadline = map["deadline"] as String,
                published_by = map["published_by"] as String
            )
        }
        fun modelToMap(issueShort: IssueShort): Map<String, String?> {
            return mapOf(
                "title" to issueShort.title,
                "description" to issueShort.description,
                "deadline" to issueShort.deadline,
                "published_by" to issueShort.published_by,
                "file_name" to issueShort.fileName,
                "download_url" to issueShort.downloadUrl
            )
        }
    }
}