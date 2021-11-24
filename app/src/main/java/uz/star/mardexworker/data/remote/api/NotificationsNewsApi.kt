package uz.star.mardexworker.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import uz.star.mardexworker.model.request.IsRead
import uz.star.mardexworker.model.response.ResponseServer
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse

/**
 * Created by Davronbek Raximjanov on 27/06/2021
 **/

interface NotificationsNewsApi {

    @GET("api/ownnotification/getAll/{id}")
    suspend fun getAllOwnNotifications(
        @Path("id") id: String
    ): Response<ResponseServer<List<OwnNotificationResponse>>>

    @PATCH("api/ownnotification/{id}")
    suspend fun changeStatusToRead(
        @Path("id") notificationId: String,
        @Body isRead: IsRead
    ): Response<ResponseServer<OwnNotificationResponse>>

}