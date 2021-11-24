package uz.star.mardexworker.ui.screen.own_notifications_activity

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.model.response.notification_for_user.OwnNotificationResponse


/**
 * Created by Davronbek Raximjanov on 27/06/2021
 **/


interface OwnNotificationsRepository {
    fun getAllOwnNotifications(userId: String): LiveData<ResultData<List<OwnNotificationResponse>>>
    fun patchToReadNotification(notificationId: String): LiveData<ResultData<OwnNotificationResponse>>
}








