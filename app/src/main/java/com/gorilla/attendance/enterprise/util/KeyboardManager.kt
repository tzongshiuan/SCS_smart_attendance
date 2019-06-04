package com.gorilla.attendance.enterprise.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/20
 * Description:
 */
object KeyboardManager {
    fun showSoftKeyboard(context: Context?, view: View?) {
        view?.requestFocus()
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}