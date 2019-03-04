package com.imaginaryrhombus.proctimer

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class TimerTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestTimerApplication::class.java.name, context)
    }
}
