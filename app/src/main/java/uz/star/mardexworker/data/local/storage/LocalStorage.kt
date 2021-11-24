package uz.star.mardexworker.data.local.storage

import android.content.Context

class LocalStorage private constructor(context: Context) {
    companion object {
        @Volatile
        lateinit var instance: LocalStorage
            private set

        fun init(context: Context) {
            instance =
                LocalStorage(
                    context
                )
        }
    }

    private val pref = context.getSharedPreferences("LocalStorage", Context.MODE_PRIVATE)

    var avatarPath: String by StringPreference(pref, "")
    var isFirst: Boolean by BooleanPreference(pref, true)
    var orderType: Boolean by BooleanPreference(pref, false)
    var logged: Boolean by BooleanPreference(pref, false)
    var completeIntro: Boolean by BooleanPreference(pref, false)
    var token: String by StringPreference(pref)
    var id: String by StringPreference(pref)

    var language: String by StringPreference(pref, "uz")
    var langLocal: String by StringPreference(pref)
    var currentLat by DoublePreference(pref, 0.0)
    var currentLong by DoublePreference(pref, 0.0)

    var expiredDate by LongPreference(pref)
    var mapState by BooleanPreference(pref, true)
    var freeState by BooleanPreference(pref, true)
    var isActive by BooleanPreference(pref)
    var lastCreatedPayment by LongPreference(pref, 0)
    var lastPaymentAmount by LongPreference(pref)

    var paymentId by StringPreference(pref)
    var notificationTime by LongPreference(pref)


    var imageMainPage: String by StringPreference(pref)
    var imagePropiskaPage by StringPreference(pref)
    var imageSelfiePage by StringPreference(pref)

    /**
     * User data(Dynamic)
     */
    var balance by LongPreference(pref, 0)
    var topCount by IntPreference(pref, 0)
    var callCount by IntPreference(pref, 0)
    var postCount by IntPreference(pref, 0)
    var name: String by StringPreference(pref)
    var phone: String by StringPreference(pref)
    var avatar: String by StringPreference(pref)
    var payId: String by StringPreference(pref)
    var versionId: Int by IntPreference(pref)
    var description: String by StringPreference(pref)

    var notificationToken: String by StringPreference(pref, "")

    fun clear() {
        pref.edit().clear().apply()
    }
}