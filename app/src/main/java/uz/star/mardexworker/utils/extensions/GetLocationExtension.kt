package uz.star.mardexworker.utils.extensions

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yandex.mapkit.geometry.Point
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.utils.DoubleBlock
import uz.star.mardexworker.utils.location.GPSTracker
import uz.star.mardexworker.utils.location.MyLocationListener

/**
 * Created by Farhod Tohirov on 21-Mar-21
 **/

fun Fragment.getLocation(point: DoubleBlock<Boolean, Point?>) {
    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) {
            var lat: Double?
            var long: Double?
            val gpsTracker = GPSTracker(requireActivity())
            gpsTracker.getLocation()
            if (gpsTracker.canGetLocation()) {
                lat = gpsTracker.getLatitude()
                long = gpsTracker.getLongitude()
                if (lat == 0.0 && long == 0.0) {
                    val locationManager =
                        requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val locationListener = MyLocationListener()

                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        5f,
                        locationListener
                    )
                    val loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        ?: locationManager.getLastKnownLocation(
                            LocationManager.NETWORK_PROVIDER
                        )
                    lat = loc?.latitude
                    long = loc?.longitude

                    if (lat != 0.0 && lat != null && long != null) {
                        val location = Point(lat, long)
                        LocalStorage.instance.currentLat = lat
                        LocalStorage.instance.currentLong = long
                        point(true, location)
                    } else {
                        point(false, null)
                    }
                } else {
                    val location = Point(lat, long)
                    LocalStorage.instance.currentLat = lat
                    LocalStorage.instance.currentLong = long
                    point(true, location)
                }
            } else {
//                showGPSAlertDialog()
            }
        }
    }
}

fun Fragment.showGPSAlertDialog() {
    val alertDialog = AlertDialog.Builder(requireContext())
        // Setting Dialog Title
        .setTitle(R.string.alert)
        // Setting Dialog Message
        .setMessage(R.string.gps_is_not_enabled)
        // On pressing Settings button
        .setPositiveButton(R.string.settings) { dialog: DialogInterface?, which: Int ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            requireContext().startActivity(intent)
        }
        // on pressing cancel button
        .setNegativeButton(R.string.cancel) { dialog: DialogInterface, which: Int -> dialog.cancel() }
        .create()
    alertDialog.setOnShowListener {
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.new_green))
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
    }
    alertDialog.show()
}
