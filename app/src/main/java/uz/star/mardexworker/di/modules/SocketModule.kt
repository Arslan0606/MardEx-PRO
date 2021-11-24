package uz.star.mardexworker.di.modules

import android.util.Log
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.net.URISyntaxException
import javax.inject.Named
import javax.inject.Singleton
import uz.star.mardexworker.utils.*


@Module
@InstallIn(SingletonComponent::class)
class SocketModule {

    @Provides
    @Named("socket")
    @Singleton
//    fun getSocketApi(): String = BASE_URL
    fun getSocketApi(): String = BASE_URL

    @Provides
    @Singleton
    fun getSocket(
        @Named("socket") socket: String
    ): Socket = try {
        Log.d("TTT", "socket module")
        IO.socket(socket)
//        IO.socket("http://188.166.158.171/")
    } catch (e: URISyntaxException) {
        Log.d("TTT", "error on socket: $e")
        throw Exception("error on socket")
    }
}