package com.imaginaryrhombus.proctimer.ui.timer

import android.content.Context
import com.google.gson.Gson
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import kotlin.math.min
import kotlin.math.sign

/**
 * 複数のタイマーを管理する Model.
 */
class MultiTimerModel(context: Context) {

    /**
     * タイマー切り替わり時のリスナーインターフェース.
     */
    interface OnTimerChangedListener {
        /**
         * タイマーが切り替わるときに呼ばれる.
         */
        fun onTimerChanged()
    }

    /**
     * 現在の動作しているタイマーを取得する.
     */
    val activeTimerModel: TimerModel
    get() {
        return timers[currentTimerIndex]
    }

    /**
     * 動作中タイマーのインデックス.
     */
    private var currentTimerIndex = 0
    set(value) {
        field = value
        onTimerChangedListener?.onTimerChanged()
    }

    /**
     * 全タイマー.
     */
    private var timers = MutableList(0) { TimerModel() }

    /**
     * 各タイマー終了時のリスナー.
     */
    var onEachTimerEndedListener: TimerModel.OnEndedListener? = null

    /**
     * タイマーが切り替わったときのリスナー.
     */
    var onTimerChangedListener: OnTimerChangedListener? = null

    /**
     * ローカルデータ読み書き用.
     */
    private val _sharedPreferences =
        context.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    /**
     * json 読み書き用.
     */
    private val gson = Gson()

    init {
        timers.clear()
        if (restoreTimerPreferences().not()) {
            for (index in 0 until TimerConstants.TIMER_DEFAULT_COUNTS) {
                timers.add(createTimerModel())
            }
            saveTimerPreferences()
        }
    }

    /**
     * タイマーを末尾に追加する.
     */
    fun addTimer() {
        timers.add(createTimerModel())
        saveTimerPreferences()
    }

    /**
     * 現在のタイマーを削除する.
     * @param onFailureListener タイマーが1つのときに削除しようとしたときの動作.
     */
    fun removeCurrentTimer(onFailureListener: () -> Unit = {}) {
        if (timers.size > 1) {
            timers.removeAt(currentTimerIndex)
            currentTimerIndex = adjustedIndexOf(currentTimerIndex)
            saveTimerPreferences()
        } else {
            onFailureListener()
        }
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
    private fun getTimer(deltaFromCurrent: Int): TimerModel {
        return timers[adjustedIndexOf(currentTimerIndex + deltaFromCurrent)]
    }

    /**
     * 指定された個数の TimeModel をアクティブに近い順に返す,
     * @param count 個数, 負数を入力するとすべてのタイマーを取得する.
     * @param includeActive 現在アクティブなタイマーを含むか.
     * @return タイマーのリスト.(個数が count よりも少ない場合はその分 null が入る)
     *
     */
    fun getTimers(count: Int, includeActive: Boolean = false): List<TimerModel?> {
        // includeActive.not のときは戦闘タイマーの数分差し引いた数を全体の数として扱う.
        val timersCount = if (includeActive) timers.size else timers.size - 1
        val retCount = if (count.sign == -1) timersCount else count
        val ret = MutableList<TimerModel?>(retCount) { null }
        for (i in 0 until min(timersCount, retCount)) {
            val timerIndex = if (includeActive) i else i + 1
            ret[i] = getTimer(timerIndex)
        }
        return ret.toList()
    }

    /**
     * アクティブなタイマーの時間を設定する.
     */
    fun setActiveTimerSeconds(seconds: Float) {
        activeTimerModel.setSeconds(seconds)
        saveTimerPreferences()
    }

    /**
     * 設定がなされた TimerModel を作成.
     * @note この関数を通さないと終了時リスナーが働かない.
     */
    private fun createTimerModel(): TimerModel {
        return TimerModel().apply {
            onEndListener = object : TimerModel.OnEndedListener {
                override fun onEnd() {
                    onTimerEnd()
                }
            }
            setSeconds(TimerConstants.TIMER_DEFAULT_SECONDS)
        }
    }

    /**
     * index をタイマーの数に丸めた値を返す.
     * @param index タイマーを指定するインデックス.
     * @return 丸められたインデックス.
     */
    private fun adjustedIndexOf(index: Int): Int {
        return index % timers.size
    }

    /**
     * タイマー終了時の動作.
     */
    private fun onTimerEnd() {
        onEachTimerEndedListener?.onEnd()
    }

    /**
     * タイマーの秒数/個数を SharedPreferences にセーブする.
     */
    private fun saveTimerPreferences() {
        val timerSecondsArray = Array(timers.size) { index -> timers[index].defaultSeconds }
        _sharedPreferences.edit().apply {
            putString(TimerConstants.PREFERENCE_PARAM_SEC_NAME,
                gson.toJson(timerSecondsArray))
            putInt(TimerConstants.PREFERENCE_SAVE_VERSION_NAME,
                TimerConstants.PREFERENCE_SAVE_VERSION)
        }.apply()
    }

    /**
     * タイマーの秒数/個数を SharedPreferences から復元する.
     * @return 復元に成功した場合は true. 失敗した場合は false.
     */
    private fun restoreTimerPreferences(): Boolean {
        val saveVersion = _sharedPreferences.getInt(
            TimerConstants.PREFERENCE_SAVE_VERSION_NAME,
            TimerConstants.PREFERENCE_SAVE_VERSION_INVALID)
        if (saveVersion == TimerConstants.PREFERENCE_SAVE_VERSION) {
            val timerSecondsJsonString =
                _sharedPreferences.getString(TimerConstants.PREFERENCE_PARAM_SEC_NAME, null)
            timerSecondsJsonString?.let { json ->
                timers.clear()
                val secondsArray = gson.fromJson(json, Array<Float>::class.java)
                secondsArray.forEach {
                    val timerModel = createTimerModel()
                    timerModel.setSeconds(it)
                    timers.add(timerModel)
                }
                return true
            }
        }
        return false
    }
}
