package com.gorilla.attendance.enterprise.ui.enroll

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Base64
import android.view.View
import androidx.navigation.Navigation
import com.gorilla.attendance.enterprise.R
import com.gorilla.attendance.enterprise.datamodel.scs.*
import com.gorilla.attendance.enterprise.util.EnterpriseUtils
import com.gorilla.attendance.enterprise.util.LOG
import com.gorilla.attendance.enterprise.util.scs.Constants
import com.gorilla.attendance.enterprise.util.scs.DebugManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/20
 */
class EnrollViewModel : ViewModel() {
    companion object {
        private val TAG = EnrollViewModel::class.java.simpleName
    }

    val isConfirmPhotoLiveData = MutableLiveData<Boolean>()

    val isWaitingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val errorLiveData = MutableLiveData<ErrorMessage>()

    var pinCode: String? = null

    var emergencyCode: String? = null

    private val compositeDisposable = CompositeDisposable()

    fun confirmPhoto() {
        LOG.D(TAG, "confirmPhoto()")

        if (DebugManager.DEBUG_MODE && DebugManager.FORCE_CONFIRM_SUCCESS) {
            isConfirmPhotoLiveData.value = true
            return
        }

        // mobileUid, username, pinCode, emergencyCode, createTime, imageList
        val body = RegisterPostBody()
        try {
            body.mobileUid = ScsPreferences.getInstance().mobileUid
            body.username = ScsPreferences.getInstance().adAccount
            body.pinCode = pinCode
            body.emergencyCode = emergencyCode

            val sdf = SimpleDateFormat(Constants.PARAM_DATE_FORMAT, Locale.getDefault())
            body.createTime = sdf.format(Date())

            val faceImage = FaceImage()
            faceImage.format = "png"
            faceImage.dataInBase64 = Base64.encodeToString(EnterpriseUtils.mFacePngList[0], Base64.DEFAULT)
            body.imageList.add(faceImage)
        } catch (e: NullPointerException) {
            LOG.E(TAG, "Null parameters, $e")
        }

        val disposableObserver = object: DisposableObserver<ScsAuthResponse>() {
            override fun onComplete() {
            }

            override fun onNext(response: ScsAuthResponse) {
                LOG.D(TAG, "onNext()")

                isWaitingLiveData.value = false

                if (response.status == ResponseStatus.SUCCESS) {
                    LOG.D(TAG, "register success")
                    isConfirmPhotoLiveData.value = true
                    ScsPreferences.getInstance().isRegister = true
                } else {
                    LOG.D(TAG, "register failed")
                    isConfirmPhotoLiveData.value = false
                    errorLiveData.value = response.error
                }
            }

            override fun onError(e: Throwable) {
                LOG.D(TAG, "onError(), $e")
                isWaitingLiveData.value = false
                isConfirmPhotoLiveData.value = false
            }
        }

        // Show loading dialog
        isWaitingLiveData.value = true

        val enrollRepository = EnrollRepository()
        compositeDisposable.add(enrollRepository.register(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableObserver))
    }

    fun getMotp(view: View) {
        Navigation.findNavController(view).navigate(R.id.action_enrollFragment_to_motpFragment)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
