package com.imaginaryrhombus.proctimer.ui.timer

import android.content.Context

/**
 * 複数のタイマーを管理する Model.
 */
class MultiTimerModel(private val context: Context) {

    /// 現在の動作しているタイマーを取得する.
    val activeTimerModel : TimerModel
    get() {
        return timers[currentTimerIndex]
    }

    /// 動作中のタイマーの数を取得する.
    val timerCount : Int
    get() {
        return timers.size
    }

    /// 動作中タイマーのインデックス.
    private var currentTimerIndex = 0

    /// 全タイマー
    private var timers = MutableList(1) { TimerModel(context) }

    /**
     * タイマーを末尾に追加する.
     */
    fun addTimer() {
        timers.add( TimerModel(context) )
    }

    /**
     * 現在のタイマーを削除する.
     */
    fun removeCurrentTimer() {
        timers.removeAt(currentTimerIndex)
        currentTimerIndex = adjustedIndexOf(currentTimerIndex)
    }

    /**
     * 現在のタイマーをリセットし、次のタイマーに移行する.
     */
    fun next() {
        activeTimerModel.reset()
        currentTimerIndex += 1
        currentTimerIndex = adjustedIndexOf(currentTimerIndex)
    }

    /**
     * index をタイマーの数に丸めた値を返す.
     * @param index タイマーを指定するインデックス.
     * @return 丸められたインデックス.
     */
    private fun adjustedIndexOf(index : Int) : Int {
        return index % timers.size
    }

    /**
     * 現在のタイマーから一定の量インデックス差分をとったタイマーを返す.
     * @param deltaFromCurrent 差分量
     * @return 現在地から差分料をとったタイマー.
     */
    fun getTimer(deltaFromCurrent : Int) : TimerModel {
        return timers[adjustedIndexOf(currentTimerIndex + deltaFromCurrent)]
    }
}
