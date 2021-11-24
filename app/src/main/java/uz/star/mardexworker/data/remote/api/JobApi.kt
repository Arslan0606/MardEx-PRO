package uz.star.mardexworker.data.remote.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import uz.star.mardexworker.model.request.AddOrRemoveWorker
import uz.star.mardexworker.model.request.FastOrderRequestData
import uz.star.mardexworker.model.request.OpportunityRequest
import uz.star.mardexworker.model.request.UpdatePassportData
import uz.star.mardexworker.model.response.*
import uz.star.mardexworker.model.response.jobs_data.AddJobRequestData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.model.response.login.WorkerResponseData
import uz.star.mardexworker.model.response.notification.NotificationResponse

/**
 * Created by Botirali Kozimov on 10/3/2021
 **/

interface JobApi {
    @GET("api/categoryjobs/lang/{lang}")
    suspend fun getJobs(@Path("lang") lang: String): Response<ResponseServer<List<JobCategory>>>

    @POST("api/users/addjobs/")
    suspend fun addJobs(@Body addJobRequestData: AddJobRequestData): Response<ResponseServer<AuthResponseData>>

    @Multipart
    @POST("api/users/upload/")
    suspend fun uploadPicture(
        @Part part: MultipartBody.Part
    ): Response<ImagePath>

    @PATCH("api/users/")
    suspend fun updateInfo(@Body data: EditProfileRequestData): Response<ResponseServer<AuthResponseData>>

    @PATCH("api/users/")
    suspend fun updateInfoHome(@Body data: EditWorkerHomeRequestData): Response<ResponseServer<AuthResponseData>>

    @PATCH("api/users/")
    suspend fun updatePassport(@Body data: UpdatePassportData): Response<ResponseServer<AuthResponseData>>


    @HTTP(method = "DELETE", path = "api/users/deleteImage/{id}", hasBody = true)
    suspend fun deleteImages(
        @Path("id") workerId: String,
        @Body ls: DeleteImagesData
    ): Response<Unit>

    @POST("api/users/updatePassword/{id}")
    suspend fun updatePassword(
        @Path("id") id: String,
        @Body() updatePasswordData: UpdatePasswordData
    ): Response<ResponseServer<AuthResponseData>>

    @POST("api/order/")
    suspend fun addFastOrder(
        @Body() fastOrderRequestData: FastOrderRequestData
    ): Response<ResponseServer<FastOrderResponseData>>

    @GET("api/order/{id}")
    suspend fun getOrders(
        @Path("id") id: String
    ): Response<ResponseServer<List<GetOrdersResponse>>>

    @PATCH("api/clients/users/")
    suspend fun deleteOrderFromClient(
        @Body data: AddOrRemoveWorker
    ): Response<ResponseServer<Any>>

    @PATCH("api/order/{id}")
    suspend fun patchFastOrder(
        @Path("id") id: String,
        @Body() fastOrderRequestData: FastOrderRequestData
    ): Response<ResponseServer<FastOrderResponseData>>

    @POST("/api/users/free")
    suspend fun setWorkerFree(): Response<WorkerResponseData>

    @POST("/api/users/unfree")
    suspend fun setWorkerUnfree(): Response<WorkerResponseData>

    @POST("/api/users/minusOpportunity/{id}")
    suspend fun minusOpportunity(
        @Path("id") id: String,
        @Body() opportunityRequest: OpportunityRequest
    ): Response<OpportunityResponse>

    @PATCH("/api/pushnotification/setUserTime/{id}")
    suspend fun getNotification(
        @Path("id") id: String,
    ): Response<ResponseServer<NotificationResponse>>
}