package com.imaginaryrhombus.proctimer.constants

/**
 * タイマーに関連する定数の定義.
 */
class TimerConstants {

    companion object {
        /// タイマーの SharedPreferences に使用する名前.
        const val PREFERENCE_NAME = "TimerSave"

        /// タイマーの秒数保存用の名前.
        const val PREFERENCE_PARAM_SEC_NAME = "seconds"

        /// タイマー未定義時の初期秒数.
        const val TIMER_DEFAULT_SECONDS = 5.0f
    }

}
