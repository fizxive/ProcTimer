package com.imaginaryrhombus.proctimer.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.imaginaryrhombus.proctimer.constants.TimerConstants

/**
 * タイマーで使用するコンポーネント群実装.
 */
class TimerComponent(app: Application) : TimerComponentInterface {

    /**
     * SharedPreferences を取得する.
     */
    override val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
}
