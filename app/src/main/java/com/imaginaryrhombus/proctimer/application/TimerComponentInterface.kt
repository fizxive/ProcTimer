package com.imaginaryrhombus.proctimer.application

import android.content.SharedPreferences

/**
 * タイマーで使用するコンポーネントのインターフェース.
 */
interface TimerComponentInterface {

    /**
     * SharedPreferences を取得する.
     */
    val sharedPreferences: SharedPreferences
}
