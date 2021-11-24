package uz.star.mardexworker.di.modules

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.star.mardexworker.utils.customlivedatas.gps.GpsStatusLiveData
import uz.star.mardexworker.utils.customlivedatas.network.ConnectionLiveData
import uz.star.mardexworker.utils.location.GPSTracker
import java.text.NumberFormat
import java.util.*
import javax.inject.Singleton

/**
 * Created by Kurganbaev Jasurbek on 06.04.2021 11:34
 **/

/** 17.04.2021 --- ---- --- -change **/


@Module
@InstallIn(SingletonComponent::class)
class HelpersModule {

    @Provides
    @Singleton
    fun getGpsTracker(
        @ApplicationContext context: Context
    ): GPSTracker =
        GPSTracker(context)

    @Provides
    @Singleton
    fun getGSonObject(): Gson =
        Gson()

    @Provides
    @Singleton
    fun getConnectionLiveData(@ApplicationContext context: Context): ConnectionLiveData =
        ConnectionLiveData(context)

    @Provides
    @Singleton
    fun getGpsLiveData(@ApplicationContext context: Context): GpsStatusLiveData =
        GpsStatusLiveData(context)

    @Provides
    @Singleton
    fun getNumberFormat(): NumberFormat = NumberFormat.getCurrencyInstance().apply {
        maximumFractionDigits = 0
        currency = Currency.getInstance("UZS")
    }
}