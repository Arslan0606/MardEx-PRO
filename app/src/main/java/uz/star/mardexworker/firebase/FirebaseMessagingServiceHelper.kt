package uz.star.mardexworker.firebase

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import uz.star.mardexworker.R
import uz.star.mardexworker.app.App
import uz.star.mardexworker.app.App.Companion.CHANNEL_ID
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.ui.screen.notification_activity.NotificationActivity


/**
 * Created by Farhod Tohirov on 16-May-21
 **/

class FirebaseMessagingServiceHelper : FirebaseMessagingService() {


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        LocalStorage.instance.notificationToken = newToken
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (!App.isForeground)
            try {
                val intent = Intent(App.instance, NotificationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                App.instance.startActivity(intent)


                /**   */
                // Create bubble intent
                val target = Intent(applicationContext, NotificationActivity::class.java)
                val bubbleIntent = PendingIntent.getActivity(applicationContext, 0, target, 0 /* flags */)

                /**   */
                val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
                var title = ""
                remoteMessage.data.values.forEachIndexed { index, text ->
                    title = if (index == 0) text else return@forEachIndexed
                }

                val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_logo_only)
                    .setContentTitle("New message")
                    .setContentText(title)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

                with(NotificationManagerCompat.from(this)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(10001520, builder)
                }
                notificationSound = MediaPlayer.create(App.instance, R.raw.notification)
                notificationSound.start()
            } catch (e: Exception) {

            }
    }

    companion object {
        var notificationSound: MediaPlayer  = MediaPlayer.create(App.instance, R.raw.notification)
    }
}