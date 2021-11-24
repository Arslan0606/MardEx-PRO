package uz.star.mardexworker.utils.extensions

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.CancellationSignal
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import uz.star.mardexworker.R
import java.util.function.Consumer


/*fun Fragment.windowSetLandscape() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}*/

/*fun AppCompatActivity.windowSetFullScreenAndOrientationLandscape() {
    this.window?.requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY)
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
}*/

/*fun Fragment.resetScreen() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
}*/

fun Fragment.showToast(string: String) {
    Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(@StringRes id: Int) {
    Toast.makeText(requireContext(), getString(id), Toast.LENGTH_SHORT).show()
}

fun Fragment.showLog(message: String, tag: String = "FRAGMENT_LOG") {
    Log.d(tag, message)
}

fun Context.getCurrentLocationCompat(locationListener: (location: Location) -> Unit) {
    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (isGPSEnabled) {
                val listener = LocationListener { location -> locationListener.invoke(location) }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    try {
                        locationManager.getCurrentLocation(
                            LocationManager.GPS_PROVIDER,
                            CancellationSignal(), mainExecutor, Consumer { location ->
                                location ?: return@Consumer
                                Log.d("CHECK_LAT", location.toString())
                                locationListener.invoke(location)
                            }
                        )
                    } catch (exception: SecurityException) {
                        Log.d("CHECK_LAT", exception.toString())
                    }
                } else {
                    try {
                        @Suppress("DEPRECATION")
                        locationManager.requestSingleUpdate(
                            LocationManager.GPS_PROVIDER, listener, null
                        )
                    } catch (exception: SecurityException) {
                        Log.d("CHECK_LAT", exception.toString())
                    }
                }
            }
        }
    }
}

fun Fragment.showMessage(text: String) {

    val dialog = AlertDialog.Builder(requireContext())
        .setTitle(text)
        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        .create()
    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
    }
    dialog.show()
}

fun Context.checkPermission(permission: String, granted: () -> Unit) {
    val mContext = this
    val options = Permissions.Options()

    options.setCreateNewTask(true)
    Permissions.check(mContext, arrayOf(permission), null, options, object : PermissionHandler() {
        override fun onGranted() {
            granted()
        }
    })
}