package com.gorilla.attendance.enterprise.util

import android.app.Activity
import android.os.Handler

/**
 * Author: Tsung Hsuan, Lai
 * Created on: 2019/2/20
 * Description: Do something after a litttle time
 */
object SimpleDelayTask {
    fun after(delay: Long, process: () -> Unit) {
        Handler().postDelayed({
            process()
        }, delay)
    }

    fun afterOnMain(delay: Long, activity: Activity, process: () -> Unit) {
        Handler().postDelayed({
            activity.runOnUiThread {
                Runnable {
                    process()
                }
            }
        }, delay)
    }
}