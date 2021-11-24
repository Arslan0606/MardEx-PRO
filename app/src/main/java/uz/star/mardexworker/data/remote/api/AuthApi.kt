package uz.star.mardexworker.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import uz.star.mardexworker.model.request.CheckData
import uz.star.mardexworker.model.request.LoginData
import uz.star.mardexworker.model.request.OrdersByStatusRequest
import uz.star.mardexworker.model.request.VerifyData
import uz.star.mardexworker.model.request.promocode.PromocodeReequestData
import uz.star.mardexworker.model.request.signup.PhoneData
import uz.star.mardexworker.model.request.signup.SignUpData
import uz.star.mardexworker.model.response.FastOrderResponseData
import uz.star.mardexworker.model.response.OpportunityResponse
import uz.star.mardexworker.model.response.ResponseServer
import uz.star.mardexworker.model.response.intro.IntroData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.balance_status.CheckPaymentStatusData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.regions.CityResponse
import uz.star.mardexworker.model.response.regions.RegionResponse

interface AuthApi {

    @POST("api/users/login")
    suspend fun login(
        @Body loginData: LoginData
    ): Response<ResponseServer<AuthResponseData>>

    @POST("api/users/signup")
    suspend fun signup(
        @Body signUpData: SignUpData
    ): Response<ResponseServer<AuthResponseData>>

    @POST("api/smsverify/receive")
    suspend fun sendSmsCode(
        @Body phoneData: PhoneData
    ): Response<ResponseServer<Any>>

    @POST("api/smsverify/check")
    suspend fun checkSmsCode(
        @Body verifyData: VerifyData
    ): Response<ResponseServer<Any>>

    @GET("api/intros/lang/{lang}")
    suspend fun getIntro(
        @Path("lang") lang: String
    ): Response<ResponseServer<List<IntroData>>>

    @POST("api/users/resetUserPassword")
    suspend fun resetUserPassword(
        @Body loginData: LoginData
    ): Response<ResponseServer<AuthResponseData>>

    @GET("api/users/{id}")
    suspend fun getWorkerData(@Path("id") workerId: String): Response<ResponseServer<WorkerResponseData>>

    @GET("api/users/checkPayment/{id}")
    suspend fun getCheckPayment(@Path("id") workerId: String): Response<ResponseServer<CheckPaymentStatusData>>

    @POST("api/admin/checkExsist")
    suspend fun checkExsist(
        @Body data: CheckData
    ): Response<OpportunityResponse>

    @POST("api/order/getByStatus/{id}")
    suspend fun getOrdersByStatus(
        @Path("id") workerId: String,
        @Body status: OrdersByStatusRequest
    ): Response<ResponseServer<List<FastOrderResponseData>>>

    @POST("api/action/participateAction")
    suspend fun sendPromocode(@Body promocodeRequestData: PromocodeReequestData): Response<ResponseServer<Int>>

    @GET("api/city")
    suspend fun getRegions(): Response<ResponseServer<List<RegionResponse>>>

    @GET("api/region")
    suspend fun getCities(): Response<ResponseServer<List<CityResponse>>>
}