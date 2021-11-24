package uz.star.mardexworker.utils.location

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log

/**
 * Created by Farhod Tohirov on 19-Dec-20
 **/

class MyLocationListener : LocationListener {
    override fun onLocationChanged(loc: Location) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }
}