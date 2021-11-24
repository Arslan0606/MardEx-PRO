package uz.star.mardexworker.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.star.mardexworker.ui.screen.entry_activity.intro_fragment.IntroRepository
import uz.star.mardexworker.ui.screen.entry_activity.intro_fragment.IntroRepositoryImpl
import uz.star.mardexworker.ui.screen.entry_activity.job_chooser_fragment.JobChooserRepository
import uz.star.mardexworker.ui.screen.entry_activity.job_chooser_fragment.JobChooserRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.profile_fragment.ProfileRepository
import uz.star.mardexworker.ui.screen.main_activity.profile_fragment.ProfileRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.profile_fragment.images_fragment.ImagesRepository
import uz.star.mardexworker.ui.screen.main_activity.profile_fragment.images_fragment.ImagesRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.TariffsRepository
import uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.TariffsRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.choose_tariff.ChooseTariffRepository
import uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.choose_tariff.ChooseTariffRepositoryImpl
import uz.star.mardexworker.ui.screen.entry_activity.login_fragment.LoginRepository
import uz.star.mardexworker.ui.screen.entry_activity.login_fragment.LoginRepositoryImpl
import uz.star.mardexworker.ui.screen.entry_activity.restore_password_fragment.RestorePasswordRepository
import uz.star.mardexworker.ui.screen.entry_activity.restore_password_fragment.RestorePasswordRepositoryImpl
import uz.star.mardexworker.ui.screen.entry_activity.signUp_fragment.SignUpRepository
import uz.star.mardexworker.ui.screen.entry_activity.signUp_fragment.SignUpRepositoryImpl
import uz.star.mardexworker.ui.screen.entry_activity.sms_verification_fragment.SmsVerificationRepository
import uz.star.mardexworker.ui.screen.entry_activity.sms_verification_fragment.SmsVerificationRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.activity_components.MainRepository
import uz.star.mardexworker.ui.screen.main_activity.activity_components.MainRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.activity_components.SocketRepository
import uz.star.mardexworker.ui.screen.main_activity.activity_components.SocketRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.edit_password.EditPasswordRepository
import uz.star.mardexworker.ui.screen.main_activity.edit_password.EditPasswordRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.edit_personal_data_fragment.EditPersonalDataRepository
import uz.star.mardexworker.ui.screen.main_activity.edit_personal_data_fragment.EditPersonalDataRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.HomeRepository
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.HomeRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.ProblemsRepository
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.ProblemsRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.show_capture_photo_fragment.ShowCameraCaptureRepository
import uz.star.mardexworker.ui.screen.main_activity.home_fragment.problems_fragment.show_capture_photo_fragment.ShowCameraCaptureRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.news_fragment.NewsRepository
import uz.star.mardexworker.ui.screen.main_activity.news_fragment.NewsRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.orders_fragment.orders_tabs_fragment.OrdersRepository
import uz.star.mardexworker.ui.screen.main_activity.orders_fragment.orders_tabs_fragment.OrdersRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.promocode.PromocodeRepository
import uz.star.mardexworker.ui.screen.main_activity.promocode.PromocodeRepositoryImpl
import uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.history_payment_fragment.HistoryPaymentRepository
import uz.star.mardexworker.ui.screen.main_activity.tariff_fragment.history_payment_fragment.HistoryPaymentRepositoryImpl
import uz.star.mardexworker.ui.screen.notification_activity.NotificationRepository
import uz.star.mardexworker.ui.screen.notification_activity.NotificationRepositoryImpl
import uz.star.mardexworker.ui.screen.own_notifications_activity.OwnNotificationsRepository
import uz.star.mardexworker.ui.screen.own_notifications_activity.OwnNotificationsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun getHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    fun getPayHistoryRepository(historyPaymentRepositoryImpl: HistoryPaymentRepositoryImpl): HistoryPaymentRepository

    @Binds
    fun getProblemsRepository(problemsRepositoryImpl: ProblemsRepositoryImpl): ProblemsRepository

    @Binds
    fun getMainRepository(mainRepositoryImpl: MainRepositoryImpl): MainRepository

    @Binds
    fun getSocketRepository(socketRepositoryImpl: SocketRepositoryImpl): SocketRepository

    @Binds
    fun getJobChooserRepository(jobChooserRepositoryImpl: JobChooserRepositoryImpl): JobChooserRepository

    @Binds
    fun getProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    fun getImagesRepository(imagesRepositoryImpl: ImagesRepositoryImpl): ImagesRepository

    @Binds
    fun getTariffsRepository(tariffsRepositoryImpl: TariffsRepositoryImpl): TariffsRepository

    @Binds
    fun getChooseTariffRepository(chooseTariffRepositoryImpl: ChooseTariffRepositoryImpl): ChooseTariffRepository

    @Binds
    fun getLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository

    @Binds
    fun getSignUpRepository(signUpRepositoryImpl: SignUpRepositoryImpl): SignUpRepository

    @Binds
    fun getSmsVerifyRepository(smsVerificationRepositoryImpl: SmsVerificationRepositoryImpl): SmsVerificationRepository

    @Binds
    fun getIntroRepository(introRepositoryImpl: IntroRepositoryImpl): IntroRepository

    @Binds
    fun getCameraCaptureRepository(cameraRepositoryImpl: ShowCameraCaptureRepositoryImpl): ShowCameraCaptureRepository

    @Binds
    fun getRestorePasswordRepository(restorePasswordRepositoryImpl: RestorePasswordRepositoryImpl): RestorePasswordRepository

    @Binds
    fun getEditPasswordRepository(editPasswordRepositoryImpl: EditPasswordRepositoryImpl): EditPasswordRepository

    @Binds
    fun getEditPersonalDataRepository(editPersonalDataRepositoryImpl: EditPersonalDataRepositoryImpl): EditPersonalDataRepository

    @Binds
    fun getOrders(ordersRepositoryImpl: OrdersRepositoryImpl): OrdersRepository

    @Binds
    fun getNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    fun getPromocodeRepository(repo: PromocodeRepositoryImpl): PromocodeRepository

    @Binds
    fun getNewsRepository(newsRepositoryImpl: NewsRepositoryImpl): NewsRepository

    @Binds
    fun getOwnNotificationsRepository(ownNotificationsRepositoryImpl: OwnNotificationsRepositoryImpl): OwnNotificationsRepository

}