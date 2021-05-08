package com.example.ess.model

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
data class User(
        val uid: String? = null,
        val name: String? = null,
        val email: String? = null,
        val imageURL: String? = null
)
class UserMapper{
    companion object{
        fun modelToMap(user: User): Map<String, String?> {
            return mapOf(
                    "name" to user.name,
                    "email" to user.email,
                    "image_url" to user.imageURL
            )
        }

        fun mapToModel(map: HashMap<String,*>): User{
            return User(
                    uid = map["uid"] as String,
                    name = map["name"] as String,
                    email = map["email"] as String,
                    imageURL = map["image_url"] as String
            )
        }
        fun dbToUser(firebaseUser: FirebaseUser):User{
            return User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName,
                    email = firebaseUser.email,
                    imageURL = firebaseUser.photoUrl.toString()
            )
        }
    }
}
@IgnoreExtraProperties
@Parcelize
data class UserShort (
       val name: String,
       val imageURL: String,
       val uid: String
        ):Parcelable
class UserShortMapper{
    companion object{
        fun modelToMap(userShort: UserShort):Map<String,Any>{
            return mapOf(
                "name" to userShort.name,
                "image_url" to userShort.imageURL,
                "uid" to userShort.uid
            )
        }

        fun mapToModel(map: HashMap<String,*>): UserShort{
            return UserShort(
                name = map["name"] as String,
                imageURL = map["image_url"] as String,
                uid = map["uid"] as String
            )
        }
    }
}

data class UserMessage(
        val name: String,
        val imageUrl: String,
        val lastMessage: String
)
class UserMessageMapper{
    companion object{
        fun modelToMap(userMessage: UserMessage):Map<String,Any>{
            return mapOf(
                    "name" to userMessage.name,
                    "image_url" to userMessage.imageUrl,
                    "last_message" to userMessage.lastMessage
            )
        }

        fun mapToModel(map: HashMap<String,*>): UserMessage{
            return UserMessage(
                    name = map["name"] as String,
                    imageUrl = map["image_url"] as String,
                    lastMessage = map["last_message"] as String
            )
        }
    }
}