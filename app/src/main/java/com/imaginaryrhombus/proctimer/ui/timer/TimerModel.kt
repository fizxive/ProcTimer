package com.imaginaryrhombus.proctimer.ui.timer

import android.content.Context
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
            seconds = preferences.getFloat("seconds", 30.0f)
            adjustSeconds()
        }
    }

    override fun close() {
        sharedPreferences?.edit()?.putFloat("seconds", seconds)?.apply()
    }

    /**
     * 時間を経過させる.
     * @param deltaSeconds 経過時間.
     */
    fun tick(deltaSeconds :Float) {
        seconds -= deltaSeconds
        adjustSeconds()
    }

    /**
     * 現在のタイマーが終了しているか.
     */
    fun isEnded() : Boolean {return seconds <= 0.0f}

    /**
     * 負の値になっていたら修正する.
     */
    private fun adjustSeconds() {
        if (seconds < 0.0f) seconds = 0.0f
    }
}
