package uz.star.mardexworker.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import uz.star.mardexworker.model.response.ResponseServer
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.Payment
import uz.star.mardexworker.model.response.tariff.PaymentResponseData
import uz.star.mardexworker.model.response.tariff.TariffData

/**
 * Created by Botirali Kozimov on 10/3/2021
 **/

interface PaymentApi {

    @GET("api/services/")
    suspend fun getTariff(): Response<ResponseServer<List<TariffData>>>

    @POST("/api/users/chooseservice/{id}")
    suspend fun chooseService(
        @Path("id") workerId: String,
        @Body payment: Payment
    ): Response<ResponseServer<AuthResponseData>>

    @POST("/api/users/payments/{payment_id}")
    suspend fun getPayments(@Path("payment_id") workerId: String): Response<PaymentResponseData>

}