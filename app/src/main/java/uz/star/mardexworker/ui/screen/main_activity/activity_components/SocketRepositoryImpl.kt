package uz.star.mardexworker.ui.screen.main_activity.activity_components

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.data.remote.SafeApiRequest
import uz.star.mardexworker.model.socket.SocketRequestData
import uz.star.mardexworker.model.socket.SocketResponseData
import uz.star.mardexworker.ui.screen.notification_activity.NotificationActivity
import uz.star.mardexworker.utils.SingleBlock
import javax.inject.Inject

/**
 * Created by Botirali Kozimov on 12/2/20.
 */

class SocketRepositoryImpl @Inject constructor(
    private val storage: LocalStorage,
    private val socket: Socket,
    private val gson: Gson
) : SocketRepository, SafeApiRequest() {

    private var userId = ""

    override val resultCallLiveData = MutableLiveData<SocketResponseData>()
    override val resultCloseDialogLiveData = MutableLiveData<Unit>()

    private val onConnect = Emitter.Listener {
        Log.d("TEST_SOCKET", "onConnect")
    }

    private val onDisconnect = Emitter.Listener {
        Log.i("TEST_SOCKET", "onDisconnect")
    }

    private val onConnectError = Emitter.Listener { it ->
        Log.i("TEST_SOCKET", "onConnectError")
        it.forEach {
            Log.i("TEST_SOCKET", "${it}")
        }
    }

    private val onClientRequest = Emitter.Listener {
        val res =
            gson.fromJson(it[0].toString(), SocketResponseData::class.java)
        if (res.workerId.equals(storage.id)) {
            Log.i("TEST_SOCKET", res.toString())
            resultCallLiveData.postValue(res)
            userId = res.clientId
        }
    }

    private val onCancelClientRequest = Emitter.Listener {
        val workersId = it[0].toString()
        Log.i("TEST_SOCKET", "onCancelClientRequest")
        if (workersId == if (userId.isEmpty()) NotificationActivity.clientId else userId) {
            resultCloseDialogLiveData.postValue(Unit)
        }
    }

    private val onWorkerResponse = Emitter.Listener {
        val res =
            gson.fromJson(it[0].toString(), SocketRequestData::class.java)
        if (userId == res.userId) {
            Log.i("TEST_SOCKET", "onWorkerResponse")
            resultCloseDialogLiveData.postValue(Unit)
            userId = ""
        }
    }

    override fun connectSocket() {
        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        socket.on("client request", onClientRequest)
        socket.on("cancel client request", onCancelClientRequest)
        socket.on("worker response", onWorkerResponse)
        socket.connect()
        Log.d("TAG_SOCKET", "connectSocket: ")
    }

    override fun userConnect(block: SingleBlock<Unit>) {
        socket.emit("connect user", storage.id)
        block.invoke(Unit)
    }

    override fun userDisconnect(block: SingleBlock<Unit>) {

    }

    override fun disconnectSocket() {
        socket.disconnect()
        socket.off(Socket.EVENT_CONNECT, onConnect)
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        Log.d("TAG_SOCKET", "disconnectSocket: ")
    }

    override fun onGetJob(it: String) {
        val socketRequestData = SocketRequestData(
            storage.id,
            it
        )
        Log.d("DIALOG_TEST", "onGetJob: $socketRequestData")
        socket.emit("worker response", gson.toJson(socketRequestData))
    }

    override fun onCancelJob(it: String) {
        val socketRequestData = SocketRequestData(
            storage.id,
            it
        )
        Log.d("DIALOG_TEST", "onGetJob: ${gson.toJson(socketRequestData)}")
        socket.emit("cancel worker request", gson.toJson(socketRequestData))
    }
}