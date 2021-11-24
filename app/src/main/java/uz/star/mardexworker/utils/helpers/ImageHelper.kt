package uz.star.mardexworker.utils.helpers

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import id.zelory.compressor.Compressor
import uz.star.mardexworker.data.remote.Coroutines
import java.io.File

/**
 * Created by Botirali Kozimov on 11-03-21
 **/

object ImageHelper {
    fun compressImage(context: Context, file: File, granted: (File?) -> Unit) {
        Coroutines.dispatcherThenMain(
            {
                Compressor.compress(context, file)
            }, {
                granted(it)
            }
        )
    }
}
