package com.imaginaryrhombus.proctimer

/// テスト用に使用する場合の便利な関数群.
class TimerModelTestUtility {
    companion object {
        /// 秒数設定のための無作為な少数を生成する.
        fun getRandomFloat(): Float {
            return Math.random().toFloat() * Float.MAX_VALUE
        }
    }
}
