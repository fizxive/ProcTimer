package com.imaginaryrhombus.proctimer.ui.timer

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.imaginaryrhombus.proctimer.TimerActivity
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import java.io.Closeable

/**
 * 一つ一つのタイマー用モデル.
 */
class TimerModel(context : Context) : Closeable {

    /// 残り秒数.
    var seconds = 0.0f

    /// ローカルデータ読み書き用.
    private val sharedPreferences = context.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    init {
        sharedPreferences?.let { preferences ->
            seconds = preferences.getFloat("seconds", 5 * 60.0f)
        }
    }

    override fun close() {
        sharedPreferences?.let { preferences ->
            preferences.edit().putFloat("seconds", seconds).apply()
        }
    }

    /**
     * 時間を経過させる.
     * @param deltaSeconds 経過時間.
     */
    fun tick(deltaSeconds :Float) {
        val nextSeconds = seconds - deltaSeconds
        seconds = if(nextSeconds > 0.0f) nextSeconds else 0.0f
    }

    /**
     * 現在のタイマーが終了しているか.
     */
    fun isEnded() {seconds <= 0.0f}
}