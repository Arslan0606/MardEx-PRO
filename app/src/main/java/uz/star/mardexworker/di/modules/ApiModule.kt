package uz.star.mardexworker.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uz.star.mardexworker.data.remote.api.AuthApi
import uz.star.mardexworker.data.remote.api.PaymentApi
import uz.star.mardexworker.data.remote.api.JobApi
import uz.star.mardexworker.data.remote.api.NewsApi
import uz.star.mardexworker.data.remote.api.NotificationsNewsApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun getAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun getJobApi(retrofit: Retrofit): JobApi = retrofit.create(JobApi::class.java)

    @Provides
    @Singleton
    fun getPaymentApi(retrofit: Retrofit): PaymentApi = retrofit.create(PaymentApi::class.java)

    @Provides
    @Singleton
    fun getNotificationsNewsApi(retrofit: Retrofit): NotificationsNewsApi = retrofit.create(NotificationsNewsApi::class.java)

    @Provides
    @Singleton
    fun getNewsApi(retrofit: Retrofit): NewsApi = retrofit.create(NewsApi::class.java)

}