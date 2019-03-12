package com.imaginaryrhombus.proctimer.application

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import com.imaginaryrhombus.proctimer.constants.TimerConstants.Companion
import org.koin.standalone.KoinComponent

/**
 * 実際の SharedPreference を扱うクラス.
 * @param timerComponentInterface コンポーネントインターフェイスを継承した実装クラス.
 */
class TimerSharedPreferencesComponent(
    timerComponentInterface: TimerComponentInterface
) : KoinComponent {

    /**
     * SharedPreferences.
     */
    private val sharedPreferences = timerComponentInterface.sharedPreferences

    /**
     * Gson オブジェクト.
     */
    private val gson = Gson()

    /**
     * List を json 化するための型情報.
     */
    private val timerSecondsTypeToken = object : TypeToken<List<Float>>() {}.type

    /**
     * タイマー秒数の初期値.
     */
    private val defaultTimerSecondsList = List(TimerConstants.TIMER_DEFAULT_COUNTS) {
        TimerConstants.TIMER_DEFAULT_SECONDS
    }

    /**
     * このアプリのセーブデータで使用するバージョン.
     */
    private val saveVersion: Int
    get() {
        return TimerConstants.PREFERENCE_SAVE_VERSION
    }

    /**
     * SharedPreferences に格納されたセーブデータのバージョン.
     */
    private var savedVersion: Int
    get() {
        return sharedPreferences
            .getInt(
                TimerConstants.PREFERENCE_SAVE_VERSION_NAME,
                TimerConstants.PREFERENCE_SAVE_VERSION_INVALID
            )
    }
    set(value) {
        sharedPreferences
            .edit()
            .putInt(
                TimerConstants.PREFERENCE_SAVE_VERSION_NAME,
                value
            )
            .apply()
    }

    init {
        if (saveVersion != savedVersion) {
            reset()
        }
    }

    /**
     * SharedPreferences を初期化する(デフォルトデータが入っている状態)
     */
    fun reset() {
        timerSecondsList = defaultTimerSecondsList
        timerTheme = Companion.TimerTheme.valueOf(TimerConstants.TIMER_THEME_DEFAULT)
        savedVersion = saveVersion
    }

    /**
     * それぞれのタイマーの秒を格納したList.
     * @note 読み出せないときは初期化された状態のものを取り出す。
     */
    var timerSecondsList: List<Float>
    get() {
        var secondsList = defaultTimerSecondsList.toMutableList()
        sharedPreferences.getString(TimerConstants.PREFERENCE_PARAM_SEC_NAME, null)?.let { json ->
            secondsList = gson.fromJson(json, timerSecondsTypeToken)
        }
        return secondsList
    }
    set(value) {
        val timerSecondsString = gson.toJson(value, timerSecondsTypeToken)
        sharedPreferences.edit()
            .putString(TimerConstants.PREFERENCE_PARAM_SEC_NAME, timerSecondsString)
            .apply()
    }

    /**
     * タイマーのテーマ設定.
     */
    var timerTheme: TimerConstants.Companion.TimerTheme
    get() {
        return Companion.TimerTheme.valueOf(requireNotNull(sharedPreferences
                .getString(TimerConstants.TIMER_THEME_NAME, TimerConstants.TIMER_THEME_DEFAULT)))

    }
    set(value) {
        sharedPreferences.edit().putString(TimerConstants.TIMER_THEME_NAME, value.name).apply()
    }
}
