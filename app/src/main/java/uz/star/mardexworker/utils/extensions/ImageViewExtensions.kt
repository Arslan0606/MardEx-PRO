package uz.star.mardexworker.utils.extensions

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import uz.star.mardexworker.R
import uz.star.mardexworker.app.App
import uz.star.mardexworker.utils.BASE_URL
import java.io.File

/**
 * Created by Botirali Kozimov on 12.03.21
 **/

fun ImageView.loadImageFile(file: File) {
    Glide.with(this).load(file).centerCrop().into(this)
}

fun ImageView.loadImageUri(uri: Uri) {
    Glide.with(this).load(uri).centerCrop().into(this)
}

fun ImageView.loadImageUrl(url: String) {
    if (url.endsWith(".svg", true)) {
        GlideToVectorYou.init()
            .with(App.instance)
            .setPlaceHolder(
                R.drawable.ic_add_image,
                R.drawable.ic_baseline_broken_image_24
            ).load(Uri.parse(BASE_URL + url), this)
    } else
        Glide.with(this)
            .load(BASE_URL + url)
            .placeholder(R.drawable.ic_add_image)
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(this)
}

fun ImageView.loadPictureUrl(url: String) {
    if (url.endsWith(".svg", true)) {
        GlideToVectorYou.init()
            .with(App.instance)
            .load(Uri.parse(BASE_URL + url), this)
    } else
        Glide.with(this)
            .load(BASE_URL + url).error(R.drawable.ic_logo)
            .into(this)
}

fun ImageView.loadImageUrl(url: String, placeHolder: Int) {
    Glide.with(this).load(url).centerCrop().placeholder(placeHolder).into(this)
}