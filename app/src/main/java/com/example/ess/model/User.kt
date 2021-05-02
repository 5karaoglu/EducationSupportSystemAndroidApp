package com.example.ess.model

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.IgnoreExtraProperties

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
                    "imageURL" to user.imageURL
            )
        }

        fun mapToModel(map: HashMap<String,*>): User{
            return User(
                    name = map["name"] as String,
                    email = map["email"] as String,
                    imageURL = map["imageURL"] as String
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
data class UserShort (
       val name: String,
       val imageURL: String
        )
class UserShortMapper{
    companion object{
        fun modelToMap(userShort: UserShort):Map<String,Any>{
            return mapOf(
                    "name" to userShort.name,
                    "imageURL" to userShort.imageURL
            )
        }

        fun mapToModel(map: HashMap<String,*>): UserShort{
            return UserShort(
                    name = map["name"] as String,
                    imageURL = map["imageURL"] as String
            )
        }
    }
}