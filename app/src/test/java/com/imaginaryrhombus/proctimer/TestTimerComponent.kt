package com.imaginaryrhombus.proctimer

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.imaginaryrhombus.proctimer.application.TimerComponentInterface
import com.imaginaryrhombus.proctimer.constants.TimerConstants

class TestTimerComponent: TimerComponentInterface {
    override val sharedPreferences: SharedPreferences
        get() {
            return ApplicationProvider.getApplicationContext<Application>()
                .getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        }
}
