package com.imaginaryrhombus.proctimer.ui.timer

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.imaginaryrhombus.proctimer.TimerActivity
import java.io.Closeable

/**
 * 一つ一つのタイマー用モデル.
 */
class TimerModel : Closeable {

    /// 残り秒数.
    var seconds = 0.0f

    /// タイマー表記の文字列. (外部からは読み込み専用)
    /// TODO : TimerViewModel に移したほうがいい?
    var text : String = ""
    private set

    /// ローカルデータ読み書き用.
    var sharedPreferences : SharedPreferences? = null

    init {
        TimerActivity.getTimerActivity()?.applicationContext?.let { context ->
            PreferenceManager.getDefaultSharedPreferences(context).let { preferences ->
                seconds = preferences.getFloat("seconds", 5 * 60.0f)
                sharedPreferences = preferences
                updateText()
            }
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
        val nextSeconds = seconds - deltaSeconds;
        seconds = if(nextSeconds > 0.0f) nextSeconds else 0.0f
        updateText()
    }

    /**
     * 現在のタイマーが終了しているか.
     */
    fun isEnded() {seconds <= 0.0f}

    /**
     * 内部の秒数をテキストに変換する.
     */
    private fun updateText() {
        val minutesInt = seconds.toInt() / 60
        val secondsInt = seconds.toInt() % 60
        text =  "%02d:%02d".format(minutesInt, secondsInt)
    }
}