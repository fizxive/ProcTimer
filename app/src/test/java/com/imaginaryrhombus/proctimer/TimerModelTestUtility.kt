package com.imaginaryrhombus.proctimer

import android.content.Context
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import com.imaginaryrhombus.proctimer.ui.timer.TimerModel

/// テスト用に使用する場合の便利な関数群.
class TimerModelTestUtility {
    companion object {
        /// 秒数設定のための無作為な少数を生成する.
        fun getRandomFloat(): Float {
            return Math.random().toFloat() * Float.MAX_VALUE
        }

        fun setTimerSecondsToSharedPreferences(seconds : Float, context : Context) {
            context.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().putFloat(TimerConstants.PREFERENCE_PARAM_SEC_NAME, seconds).apply()
        }

        /** テスト用の TimerModel を作成する.
         * @param seconds 設定する秒数.
         * @see TimerModel
         */
        fun createTimerModel(seconds: Float, context: Context): TimerModel {
            setTimerSecondsToSharedPreferences(seconds, context)
            return TimerModel(context)
        }
    }
}
