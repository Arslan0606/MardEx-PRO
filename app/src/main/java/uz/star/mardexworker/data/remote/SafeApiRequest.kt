package uz.star.mardexworker.data.remote

import android.util.Log
import retrofit2.Response
import uz.star.mardexworker.R
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.local.ResultData
import uz.star.mardexworker.utils.NOT_REGISTERED

abstract class SafeApiRequest {
    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): ResultData<T>? {
        return try {
            val response = call.invoke()

            if (response.code() == 90)
                return ResultData.messageData(MessageData.resource(R.string.token_expire))

            if (response.code() == 800)
                return ResultData.messageData(MessageData.message(NOT_REGISTERED))

            if (response.code() == 501) {
                return ResultData.messageData(MessageData.resource(R.string.code_incorrect))
            }

            if (response.code() == 502) {
                return ResultData.messageData(MessageData.resource(R.string.sms_is_not_working))
            }

            if (response.code() == 500) {
                return ResultData.messageData(MessageData.resource(R.string.duplicat_key))
            }

            if (response.code() >= 500) {
                return ResultData.messageData(MessageData.resource(R.string.try_after_a_while))
            }

            if (response.code() == 401) {
                return ResultData.messageData(MessageData.resource(R.string.incorrect_phone_number))
            }

            if (response.code() in 400..500) {
                return ResultData.messageData(MessageData.resource(R.string.failure))
            }
            if (response.body() == null)
                ResultData.messageData(MessageData.resource(R.string.failure))
            else ResultData.data(response.body()!!)

        } catch (e: Exception) {
            Log.e("SERVER_LOG", "apiRequest: ${e.message}")
            when (e) {
                is java.net.ConnectException -> ResultData.messageData(MessageData.resource(R.string.turn_on_internet))
                is java.net.SocketTimeoutException -> ResultData.messageData(MessageData.resource(R.string.internet_slow))
                else-> ResultData.messageData(MessageData.resource(R.string.failure))
            }
        }
    }
}