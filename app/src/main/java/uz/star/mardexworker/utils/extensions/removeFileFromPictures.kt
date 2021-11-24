package uz.star.mardexworker.utils.extensions

import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import java.io.File

/**
 * Created by Farhod Tohirov on 27-Mar-21
 **/

fun Fragment.removeFileFromPictures(file: File) {
    try {
        val externalFilesDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (externalFilesDir?.isDirectory == true) {
            val listFiles = externalFilesDir.listFiles()
            if (listFiles?.isNotEmpty() == true) {
                listFiles.forEach { fileInFolder ->
                    if (fileInFolder.name == file.name) {
                        fileInFolder.delete()
                        return
                    }
                }
            }
        }
    } catch (e: Exception) {
    }
}

fun Fragment.clearImagesFromCache() {
    try {
        val externalFilesDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (externalFilesDir?.isDirectory == true) {
            val listFiles = externalFilesDir.listFiles()
            if (listFiles?.isEmpty() == true) {
                return
            }
            listFiles?.forEach {
                it.delete()
            }
        }
    } catch (e: Exception) {
    }
}
