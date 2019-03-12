package com.imaginaryrhombus.proctimer.constants

/**
 * タイマーに関連する定数の定義.
 */
class TimerConstants {

    companion object {
        /**
         * 保存データのバージョンを管理する名前.
         */
        const val PREFERENCE_SAVE_VERSION_NAME = "SaveVersion"

        /**
         * 保存データのバージョン.
         */
        const val PREFERENCE_SAVE_VERSION = 4

        /**
         * 保存データのバージョンが読めなかったときのデフォルト値.
         */
        const val PREFERENCE_SAVE_VERSION_INVALID = -1

        /**
         * タイマーの SharedPreferences に使用する名前.
         */
        const val PREFERENCE_NAME = "TimerSave"

        /**
         * タイマーの秒数保存用の名前.
         */
        const val PREFERENCE_PARAM_SEC_NAME = "seconds"

        /**
         * タイマー未定義時の初期秒数.
         */
        const val TIMER_DEFAULT_SECONDS = 60.0f

        /**
         * タイマーの個数が未定義時の初期個数.
         */
        const val TIMER_DEFAULT_COUNTS = 2

        /**
         * タイマーのテーマ保存用の名前.
         */
        const val TIMER_THEME_NAME = "theme"

        enum class TimerTheme {
            Light,
            Dark;
        }

        /**
         * タイマーのテーマデフォルト値.
         */
        val TIMER_THEME_DEFAULT = TimerTheme.Light.name
    }
}
