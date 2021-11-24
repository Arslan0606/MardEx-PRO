package uz.star.mardexworker.app

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import uz.star.mardexworker.data.local.storage.LocalStorage
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import uz.star.mardexworker.R
import uz.star.mardexworker.utils.darkmode.ModeChanger

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        LocalStorage.init(this)
        MapKitFactory.setApiKey("8533b7ec-ffbc-4132-b951-99530aeb1882")
        ModeChanger.getStatusMode(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        lateinit var instance: App
        var isForeground = false
        val CHANNEL_ID = "CHANNEL FOR NOTIFICATION"
    }
}



