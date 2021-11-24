package uz.star.mardexworker.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import uz.star.mardexworker.model.response.ResponseServer
import uz.star.mardexworker.model.response.news.NewsData

/**
 * Created by Jasurbek Kurganbaev on 27.06.2021 14:46
 **/
interface NewsApi {

    @GET("api/news/getAll")
    suspend fun getNews(): Response<ResponseServer<List<NewsData>>>
}