package com.example.ess.data

import com.example.ess.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

const val serverKey = "AAAAklewo7c:APA91bFxTCEMjFzcNLbee3wBOjY6pL-XsTy9wjhYOn-m1I1KAVLiCf5l2cGHt7A3GypGK-rGY7Owi16W_T2sdvlnfo554tTtE9vaaF9B6xP7RPd5Taujfhfbr5rjaE6VDtN3pfNEuheJ"
const val contentType = "application/json"

interface NotificationApi {


    @Headers("Authorization: key=$serverKey", "Content-Type:$contentType")
    @POST("/fcm/send")
    suspend fun postNotification(
            @Body notification: PushNotification
    ): Response<ResponseBody>
}