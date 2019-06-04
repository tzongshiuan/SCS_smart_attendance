package com.gorilla.attendance.enterprise.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import androidx.navigation.Navigation
import com.gorilla.attendance.enterprise.BuildConfig
import com.gorilla.attendance.enterprise.R
import com.gorilla.attendance.enterprise.datamodel.scs.ScsPreferences
import com.gorilla.attendance.enterprise.datamodel.scs.networkChecker.NetworkChangeReceiver
import com.gorilla.attendance.enterprise.datamodel.scs.networkChecker.NetworkState
import com.gorilla.attendance.enterprise.util.LOG
import com.gorilla.attendance.enterprise.util.SimpleDelayTask
import com.gorilla.attendance.enterprise.util.scs.Constants
import com.gorilla.attendance.enterprise.util.scs.DebugManager
import kotlinx.android.synthetic.main.scs_main_activity.*

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/19
 */
class ScsMainActivity : AppCompatActivity() {

    companion object {
        private val TAG = ScsMainActivity::class.java.simpleName

        private const val PERMISSION_ALL = 1

        private const val MSG_NETWORK_DIALOG = 16

        var IS_READY = false

        class NetworkHandler(val activity: ScsMainActivity): Handler() {

            override fun handleMessage(msg: Message) {
                LOG.D(TAG, "handleMessage msg.what = " + msg.what)
                when (msg.what) {
                    MSG_NETWORK_DIALOG -> {
                        activity.showNetworkCheckDialog()
                    }
                }
            }
        }
    }

    private val networkReceiver: NetworkChangeReceiver by lazy { NetworkChangeReceiver() }

    private var networkDialog: AlertDialog? = null

    private val handler: Handler = NetworkHandler(this)

    private val mPermissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.D(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scs_main_activity)

        if(!hasPermissions()){
            ActivityCompat.requestPermissions(this, mPermissions, PERMISSION_ALL)
        }

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                    .replace(R.id.container, LoginFragment.newInstance())
//                    .commitNow()
//        }

//        val host: NavHostFragment = supportFragmentManager
//                .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        ScsPreferences.getInstance().readPreferences(this)
        setDeviceUid()

        DebugManager.DEBUG_MODE = BuildConfig.DEBUG

        // About network checker
        NetworkState.isNetworkConnected.observe(this, Observer {
            LOG.D(TAG, "Is network connected = ${it!!}")

            if (!it) {
                handler.sendEmptyMessageDelayed(MSG_NETWORK_DIALOG, 1500)
            } else {
                handler.removeMessages(MSG_NETWORK_DIALOG)
            }
        })
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
    }

    override fun onResume() {
        LOG.D(TAG, "onResume()")
        super.onResume()

        initSettings()
    }

    override fun onStop() {
        LOG.D(TAG, "onStop()")
        super.onStop()

        ScsPreferences.getInstance().savePreferences(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        LOG.D(TAG, "onDestroy()")

        unregisterReceiver(networkReceiver)
    }

    override fun onBackPressed() {
        val currentFragment = my_nav_host_fragment.childFragmentManager.fragments[0]
        val controller = Navigation.findNavController(this, R.id.my_nav_host_fragment)

        // TODO handle all fragments' backpress() event here
//        if (currentFragment is LoginFragment) {
//            finish()
//        } else if (!controller.popBackStack()) {
//            finish()
//        }

//        if (currentFragment is OnBackPressedListener)
//            (currentFragment as OnBackPressedListener).onBackPressed()
//        else if (!controller.popBackStack())
//            finish()

//        public interface OnBackPressedListener {
//            void onBackPressed();
//        }
    }

    private fun hasPermissions(): Boolean {
        for (permission in mPermissions) {
            if (this.let { ContextCompat.checkSelfPermission(it, permission) } != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_ALL -> {
                var isAllGrant = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        isAllGrant = false
                        break
                    }
                }

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && isAllGrant) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initSettings()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Request permission failed, forced to close app", Toast.LENGTH_SHORT).show()
                    SimpleDelayTask.after(Constants.SPLASH_DELAY_TIME) {
                        this.finish()
                    }
                }
                return
            }
        }

        // other 'case' lines to check for other
        // permissions this app might request
    }

    @SuppressLint("HardwareIds")
    private fun setDeviceUid() {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        ScsPreferences.getInstance().mobileUid = androidId
    }

    private fun initSettings() {
        turnGpsOn()
    }

    private fun turnGpsOn() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //ask to turn gps on
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.location_permission))
                    .setCancelable(false)
                    .setPositiveButton("Yes") {
                        dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setNegativeButton("No") {
                        dialog, id -> finish()
                    }
            val alert = builder.create()
            alert.show()
        } else {
            IS_READY = true
        }
    }

    private fun showNetworkCheckDialog() {
        if (networkDialog == null) {
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(false)
            builder.setTitle(getString(R.string.txt_network_check))
            builder.setMessage(getString(R.string.msg_network_check))
            builder.setPositiveButton(getString(R.string.txt_log_ok)) { _, _ ->
                this.finish()
            }
            networkDialog = builder.create()
        }
        networkDialog?.show()
    }
}
