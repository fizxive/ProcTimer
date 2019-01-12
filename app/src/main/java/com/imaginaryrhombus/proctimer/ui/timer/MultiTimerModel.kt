package com.imaginaryrhombus.proctimer.ui.timer

import android.content.Context
import java.util.*
import kotlin.math.min

/**
 * 複数のタイマーを管理する Model.
 */
class MultiTimerModel(private val context: Context) {

    /// タイマー切り替わり時のリスナーインターフェース.
    interface OnTimerChangedListener {
        /**
         * タイマーが切り替わるときに呼ばれる.
         */
        fun onTimerChanged()
    }

    /// 現在の動作しているタイマーを取得する.
    val activeTimerModel : TimerModel
    get() {
        return _timers[currentTimerIndex]
    }

    /// 動作中タイマーのインデックス.
    private var currentTimerIndex = 0
    set(value) {
        field = value
        onTimerChangedListener?.onTimerChanged()
    }

    /// 全タイマー
    private var _timers = MutableList(3) { createTimerModel(context) }

    /// 各タイマー終了時のリスナー.
    var onEachTimerEndedListener : TimerModel.OnEndedListener? = null

    /// タイマーが切り替わったときのリスナー.
    var onTimerChangedListener : OnTimerChangedListener? = null

    /**
     * タイマーを末尾に追加する.
     */
    fun addTimer() {
        _timers.add( TimerModel(context) )
    }

    /**
     * 現在のタイマーを削除する.
     */
    fun removeCurrentTimer() {
        _timers.removeAt(currentTimerIndex)
        currentTimerIndex = adjustedIndexOf(currentTimerIndex)
    }

    /**
     * 現在のタイマーをリセットし、次のタイマーに移行する.
     */
    fun next() {
        activeTimerModel.reset()
        currentTimerIndex = adjustedIndexOf(currentTimerIndex + 1)
    }

    /**
     * 現在のタイマーから一定の量インデックス差分をとったタイマーを返す.
     * @param deltaFromCurrent 差分量
     * @return 現在地から差分料をとったタイマー.
     */
    private fun getTimer(deltaFromCurrent : Int) : TimerModel {
        return _timers[adjustedIndexOf(currentTimerIndex + deltaFromCurrent)]
    }

    /**
     * 指定された個数の TimeModel をアクティブに近い順に返す,
     * @param count 個数.
     * @param includeActive 現在アクティブなタイマーを含むか.
     * @return タイマーのリスト.(個数が count よりも少ない場合はその分 null が入る)
     *
     */
    fun getTimers(count: Int, includeActive: Boolean = false) : List<TimerModel?> {
        val ret = MutableList<TimerModel?>(count) { null }
        // includeActive が false の場合、アクティブタイマーの分だけ数を減らして考える.
        val timerCount = _timers.size - (if (includeActive.not()) 1 else 0)
        for (i in 0 until (min(timerCount, count))) {
            val timerIndex = if(includeActive) i else i + 1
            ret[i] = getTimer(timerIndex)
        }
        return ret.toList()
    }

    /**
     * 設定がなされた TimerModel を作成.
     * @note この関数を通さないと終了時リスナーが働かない.
     */
    private fun createTimerModel(context: Context) : TimerModel {
        return TimerModel(context).apply {
            onEndListener = object : TimerModel.OnEndedListener{
                override fun onEnd() {
                    onTimerEnd()
                }
            }
        }
    }

    /**
     * index をタイマーの数に丸めた値を返す.
     * @param index タイマーを指定するインデックス.
     * @return 丸められたインデックス.
     */
    private fun adjustedIndexOf(index : Int) : Int {
        return index % _timers.size
    }

    /**
     * タイマー終了時の動作.
     */
    private fun onTimerEnd() {
        onEachTimerEndedListener?.onEnd()
    }
}
