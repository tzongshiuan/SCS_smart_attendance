package com.gorilla.attendance.enterprise.ui.login

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import androidx.navigation.Navigation
import com.gorilla.attendance.enterprise.R
import com.gorilla.attendance.enterprise.datamodel.scs.LoginResult
import com.gorilla.attendance.enterprise.datamodel.scs.ResponseStatus
import com.gorilla.attendance.enterprise.datamodel.scs.ScsAuthResponse
import com.gorilla.attendance.enterprise.datamodel.scs.ScsPreferences
import com.gorilla.attendance.enterprise.util.LOG
import com.gorilla.attendance.enterprise.util.scs.DebugManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import retrofit2.HttpException

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/19
 */
class LoginViewModel : ViewModel() {
    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }

    var adAccount: String? = null
    var adPassword: String? = null

    val loginResultLiveData = MutableLiveData<Int>()

    private val compositeDisposable = CompositeDisposable()

    fun login(view: View) {
        LOG.D(TAG, "Do login, account: $adAccount, password: $adPassword")

        if (DebugManager.DEBUG_MODE and DebugManager.FORCE_AD_AUTH_SUCCESS) {
            ScsPreferences.getInstance().adAccount = DebugManager.DEFAULT_AD_ACCOUNT
            ScsPreferences.getInstance().adPassword = DebugManager.DEFAULT_AD_PASSWORD
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_pinRegisterFragment)
            return
        }

        // Handle invalid situations
        if (adAccount.isNullOrEmpty()) {
            loginResultLiveData.value = LoginResult.EMPTY_AD_ACCOUNT
            return
        }

        if (adPassword.isNullOrEmpty()) {
            loginResultLiveData.value = LoginResult.EMPTY_AD_PASSWORD
            return
        }

        // Start login
        val disposableObserver = object : DisposableObserver<ScsAuthResponse>() {
            override fun onComplete() {
            }

            override fun onNext(response: ScsAuthResponse) {
                LOG.D(TAG, "onNext()")

                if (response.status == ResponseStatus.SUCCESS) {
                    LOG.D(TAG, "login success")
                    ScsPreferences.getInstance().adAccount = adAccount
                    ScsPreferences.getInstance().adPassword = adPassword
                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_pinRegisterFragment)
                } else {
                    LOG.D(TAG, "login failed")
                    loginResultLiveData.value = LoginResult.WRONG_AD_INFO
                }
            }

            override fun onError(e: Throwable) {
                LOG.D(TAG, "onError(), $e")
            }
        }

        val loginRepository = LoginRepository()
        compositeDisposable.add(loginRepository.login(adAccount!!, adPassword!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableObserver))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
