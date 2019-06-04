package com.gorilla.attendance.enterprise.util.scs

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.widget.Toast
import com.gorilla.attendance.enterprise.ui.motp.MotpViewModel
import com.gorilla.attendance.enterprise.util.LOG
import org.w3c.dom.Text

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/3/4
 * Description:
 */
class MyLocationListener : LocationListener {

    companion object {
        private val TAG = MotpViewModel::class.java.simpleName
    }

    var longitude: Double? = 0.0
    var latitude: Double? = 0.0
    var altitude: Double? = 0.0

    override fun onLocationChanged(location: Location?) {
        this.longitude = location?.longitude
        this.latitude = location?.latitude
        this.altitude = location?.altitude

        LOG.D(TAG, "onLocationChanged(), longitude: $longitude, latitude: $latitude, altitude: $altitude")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        LOG.D(TAG, "onStatusChanged()")
    }

    override fun onProviderEnabled(provider: String?) {
        LOG.D(TAG, "onProviderEnabled()")
    }

    override fun onProviderDisabled(provider: String?) {
        LOG.D(TAG, "onProviderDisabled()")
    }
}