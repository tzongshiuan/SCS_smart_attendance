package com.gorilla.attendance.enterprise.ui.motp

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.location.LocationManager
import android.location.LocationProvider
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation
import com.gorilla.attendance.enterprise.R
import com.gorilla.attendance.enterprise.datamodel.scs.*
import com.gorilla.attendance.enterprise.util.EnterpriseUtils
import com.gorilla.attendance.enterprise.util.LOG
import com.gorilla.attendance.enterprise.util.scs.Constants
import com.gorilla.attendance.enterprise.util.scs.DebugManager
import com.gorilla.attendance.enterprise.util.scs.MyLocationListener
import com.otp.hkdf.OtpUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/21
 */
class MotpViewModel : ViewModel() {
    companion object {
        private val TAG = MotpViewModel::class.java.simpleName

        const val UI_ENTER_PIN_CODE      = 0
        const val UI_FACE_DETECTING      = 1
        const val UI_UNAUTHORIZED_USER   = 2
        const val UI_CAN_NOT_DETECT_FACE = 3
        const val UI_GET_MOTP_SUCCESS    = 4

        const val FDR_VERIFY_SUCCESS   = 0
        const val FDR_VERIFY_FAILED    = 1
        const val FDR_CANT_DETECT_FACE = 2
    }

    lateinit var locationManager: LocationManager
    lateinit var locationProvider: LocationProvider
    val locationListener = MyLocationListener()

    val uiModeLiveData = MutableLiveData<Int>()

    init {
        uiModeLiveData.value = UI_ENTER_PIN_CODE
    }

    var pinCode: String? = null

    var motpLiveData = MutableLiveData<String>()

    private val compositeDisposable = CompositeDisposable()

    val errorLiveData = MutableLiveData<ErrorMessage>()

    private var mLastVerifyData: String? = null

    private var mLastUserId: String? = null
    private var mLastLoginMode: String? = null

    fun initSetting(activity: Activity?) {
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0f, locationListener)
        } catch (e: SecurityException) {
            LOG.E(TAG, e.toString())
        }
    }

    fun confirmPinCode(context: Context?) {
        LOG.D(TAG, "Do login, pinCode: $pinCode")

        if (DebugManager.DEBUG_MODE && DebugManager.FORCE_SA_AUTH_SUCCESS) {
            if (DebugManager.DEBUG_MODE && DebugManager.SKIP_FACE_VERIFY) {
                verifyFaceData(ByteArray(1))
                return
            }
            uiModeLiveData.value = UI_FACE_DETECTING
            return
        }

        // Handle invalid situations
        if (pinCode.isNullOrEmpty()) {
            Toast.makeText(context, R.string.msg_pin_code_empty, Toast.LENGTH_SHORT).show()
            return
        }

        val disposableObserver = object: DisposableObserver<ScsAuthResponse>() {
            override fun onComplete() {
            }

            override fun onNext(response: ScsAuthResponse) {
                LOG.D(TAG, "onNext()")

                if (response.status == ResponseStatus.SUCCESS) {
                    LOG.D(TAG, "sa auth success")

                    mLastUserId = response.data?.id
                    mLastLoginMode = response.data?.loginMode
                    uiModeLiveData.value = UI_FACE_DETECTING
                } else {
                    LOG.D(TAG, "sa auth failed")
                    errorLiveData.value = response.error
                }
            }

            override fun onError(e: Throwable) {
                LOG.D(TAG, "onError(), $e")
            }
        }

        val body = SaAuthPostBody()
        body.mobileUid = ScsPreferences.getInstance().mobileUid
        body.username = ScsPreferences.getInstance().adAccount
        body.pinCode = pinCode

        val motpRepository = MotpRepository()
        compositeDisposable.add(motpRepository.saAuth(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableObserver))
    }

    /**
     * Create new account
     */
    fun createAdAccount(view: View) {
        ScsPreferences.getInstance().isRegister = false
        Navigation.findNavController(view).navigate(R.id.action_motpFragment_to_loginFragment)
    }

    /**
     * Means to get MOTP again
     */
    fun done() {
        uiModeLiveData.value = UI_ENTER_PIN_CODE
    }

    fun verifyFaceData(faceData: ByteArray) {
        LOG.D(TAG, "verifyFaceData()")

        if (DebugManager.DEBUG_MODE && DebugManager.FORCE_VERIFY_SUCCESS) {
            onReceiveFdrResult(FDR_VERIFY_SUCCESS)
            return
        }

        // send BAP verify command
        if (faceData.isEmpty()) {
            LOG.E(TAG, "Face image is empty !!")
            return
        }

        val disposableObserver = object: DisposableObserver<ScsAuthResponse>() {
            override fun onComplete() {
            }

            override fun onNext(response: ScsAuthResponse) {
                LOG.D(TAG, "onNext()")

                if (response.status == ResponseStatus.SUCCESS) {
                    LOG.D(TAG, "bap verify success")
                    onReceiveFdrResult(FDR_VERIFY_SUCCESS)
                } else {
                    LOG.D(TAG, "bap verify failed")
                    errorLiveData.value = response.error
                    // TODO parser result to match different error type
                    onReceiveFdrResult(FDR_VERIFY_FAILED)
                }
            }

            override fun onError(e: Throwable) {
                LOG.D(TAG, "onError(), $e")
            }
        }

        val body = ScsBapVerifyPostBody()
        body.id = mLastUserId
        body.type = VerifyType.TYPE_EMPLOYEE

        val faceImage = FaceImage()
        faceImage.format = "png"
        mLastVerifyData = Base64.encodeToString(EnterpriseUtils.mFacePngList[0], Base64.DEFAULT)
        faceImage.dataInBase64 = mLastVerifyData
        body.imageList.add(faceImage)

        val motpRepository = MotpRepository()
        compositeDisposable.add(motpRepository.bapVerify(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableObserver))
    }

    fun onReceiveFdrResult(retCode: Int) {
        when (retCode) {
            FDR_VERIFY_SUCCESS -> {
                // Generate OTP code
                motpLiveData.value = OtpUtil.generateOTP(ScsPreferences.getInstance().adAccount)
                LOG.D(TAG, "Account: ${ScsPreferences.getInstance().adAccount}")

                // If need to change parameters of OTP algorithm
                //val param = OtpParam()
                //param.timeStep = 3
                //param.passwordLength = OtpParam.PasswordLength.SEVEN
                //param.algorithm = OtpParam.TotpAlgorithm.HMAC_SHA1
                //param.salt = "3b128023154c0ade8fd28106a11d1156"
                //motpLiveData.value = OtpUtil.generateOTP(ScsPreferences.getInstance().adAccount, param)

                uiModeLiveData.value = UI_GET_MOTP_SUCCESS
            }

            FDR_VERIFY_FAILED -> {
                uiModeLiveData.value = UI_UNAUTHORIZED_USER
            }

            FDR_CANT_DETECT_FACE -> {
                uiModeLiveData.value = UI_CAN_NOT_DETECT_FACE
            }

            else -> {}
        }

        if (DebugManager.DEBUG_MODE && DebugManager.SKIP_SEND_RECORD) {
            return
        }

        if (retCode == FDR_VERIFY_SUCCESS) {
            sendScsRecord(FaceVerifyStatus.SUCCEED)
        } else {
            sendScsRecord(FaceVerifyStatus.FAILED)

            // Optional API when verified failed
            sendScsUnrecognizedLog()
        }
    }

    private fun getGpsInfo(): GpsInfo {
        val gpsInfo = GpsInfo()
        gpsInfo.longitude = locationListener.longitude.toString()
        gpsInfo.latitude = locationListener.latitude.toString()
        gpsInfo.altitude = locationListener.altitude.toString()
        return gpsInfo
    }

    @Synchronized
    private fun sendScsRecord(verifyStatus: String) {
        LOG.D(TAG, "sendScsRecord()")

        // Send verify record to server
        val record = ScsRecord()
        record.serial = ScsPreferences.getInstance().scsRecordSerial++
        record.username = ScsPreferences.getInstance().adAccount
        record.pinCode = pinCode
        val sdf = SimpleDateFormat(Constants.PARAM_DATE_FORMAT, Locale.getDefault())
        record.deviceTime = sdf.format(Date())
        record.faceVerify = verifyStatus
        record.faceImg = mLastVerifyData
        record.mobileUid = ScsPreferences.getInstance().mobileUid
        record.loginMode = mLastLoginMode
        record.gps = getGpsInfo()
        ScsPreferences.getInstance().scsRecordList.add(record)

        val disposableObserver = object: DisposableObserver<ScsAuthResponse>() {
            override fun onComplete() {
            }

            override fun onNext(response: ScsAuthResponse) {
                LOG.D(TAG, "onNext()")

                if (response.status == ResponseStatus.SUCCESS) {
                    LOG.D(TAG, "send record to server success")

                    // clear all records already sent to the server
                    ScsPreferences.getInstance().scsRecordList.clear()
                } else {
                    LOG.D(TAG, "send record to server failed")
                    errorLiveData.value = response.error
                }
            }

            override fun onError(e: Throwable) {
                LOG.D(TAG, "onError(), $e")
            }
        }

        val body = ScsRecordPostBody()
        body.records = ScsPreferences.getInstance().scsRecordList

        val motpRepository = MotpRepository()
        compositeDisposable.add(motpRepository.scsRecords(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableObserver))
    }

    @Synchronized
    private fun sendScsUnrecognizedLog() {
        LOG.D(TAG, "sendScsUnrecognizedLog()")

        // Send unrecognized log to server
        val log = ScsErrorLog()
        log.serial = ScsPreferences.getInstance().scsErrorLogSerial++
        val sdf = SimpleDateFormat(Constants.PARAM_DATE_FORMAT, Locale.getDefault())
        log.deviceTime = sdf.format(Date())
        log.faceImg = mLastVerifyData
        log.username = ScsPreferences.getInstance().adAccount
        log.mobileUid = ScsPreferences.getInstance().mobileUid
        log.pinCode = pinCode
        log.gps = getGpsInfo()
        ScsPreferences.getInstance().scsErrorLogList.add(log)

        val disposableObserver = object: DisposableObserver<ScsAuthResponse>() {
            override fun onComplete() {
            }

            override fun onNext(response: ScsAuthResponse) {
                LOG.D(TAG, "onNext()")

                if (response.status == ResponseStatus.SUCCESS) {
                    LOG.D(TAG, "send unrecognized log to server success")

                    // clear all records already sent to the server
                    ScsPreferences.getInstance().scsErrorLogList.clear()
                } else {
                    LOG.D(TAG, "send unrecognized log  to server failed")
                    errorLiveData.value = response.error
                }
            }

            override fun onError(e: Throwable) {
                LOG.D(TAG, "onError(), $e")
            }
        }

        val body = ScsUnrecognizePostBody()
        body.errorLogs = ScsPreferences.getInstance().scsErrorLogList

        val motpRepository = MotpRepository()
        compositeDisposable.add(motpRepository.scsUnrecognizedLog(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableObserver))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
