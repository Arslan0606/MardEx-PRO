package uz.star.mardexworker.ui.screen.main_activity.activity_components

import androidx.lifecycle.LiveData
import uz.star.mardexworker.model.socket.SocketResponseData
import uz.star.mardexworker.utils.SingleBlock

/**
 * Created by Botirali Kozimov on 12/2/20.
 */

interface SocketRepository {
    val resultCallLiveData: LiveData<SocketResponseData>
    val resultCloseDialogLiveData: LiveData<Unit>

    fun userConnect(block: SingleBlock<Unit>)
    fun userDisconnect(block: SingleBlock<Unit>)
    fun disconnectSocket()
    fun onGetJob(it: String)
    fun onCancelJob(it: String)
    fun connectSocket()
}