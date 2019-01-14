package com.imaginaryrhombus.proctimer.ui.timer

import android.content.Context
import com.google.gson.Gson
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import kotlin.math.min

/**
 * 複数のタイマーを管理する Model.
 */
class MultiTimerModel(context: Context) {

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
        return _timers[_currentTimerIndex]
    }

    /// 動作中タイマーのインデックス.
    private var _currentTimerIndex = 0
    set(value) {
        field = value
        onTimerChangedListener?.onTimerChanged()
    }

    /// 全タイマー.
    private var _timers = MutableList(0) { TimerModel() }

    /// 各タイマー終了時のリスナー.
    var onEachTimerEndedListener : TimerModel.OnEndedListener? = null

    /// タイマーが切り替わったときのリスナー.
    var onTimerChangedListener : OnTimerChangedListener? = null

    /// ローカルデータ読み書き用.
    private val _sharedPreferences = context.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    /// json 読み書き用.
    private val _gson = Gson()

    init {
        _timers.clear()
        if (restoreTimerPreferences().not()) {
            for (index in 0 until TimerConstants.TIMER_DEFAULT_COUNTS) {
                val timer = createTimerModel()
                _timers.add(timer)
            }
            saveTimerPreferences()
        }
    }

    /**
     * タイマーを末尾に追加する.
     */
    fun addTimer() {
        _timers.add(createTimerModel())
        saveTimerPreferences()
    }

    /**
     * 現在のタイマーを削除する.
     */
    fun removeCurrentTimer() {
        _timers.removeAt(_currentTimerIndex)
        _currentTimerIndex = adjustedIndexOf(_currentTimerIndex)
        saveTimerPreferences()
    }

    /**
     * 現在のタイマーをリセットし、次のタイマーに移行する.
     */
    fun next() {
        activeTimerModel.reset()
        _currentTimerIndex = adjustedIndexOf(_currentTimerIndex + 1)
    }

    /**
     * 現在のタイマーから一定の量インデックス差分をとったタイマーを返す.
     * @param deltaFromCurrent 差分量
     * @return 現在地から差分料をとったタイマー.
     */
    private fun getTimer(deltaFromCurrent : Int) : TimerModel {
        return _timers[adjustedIndexOf(_currentTimerIndex + deltaFromCurrent)]
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
     * アクティブなタイマーの時間を設定する.
     */
    fun setActiveTimerSeconds(seconds : Float) {
        activeTimerModel.setSeconds(seconds)
        saveTimerPreferences()
    }

    /**
     * 設定がなされた TimerModel を作成.
     * @note この関数を通さないと終了時リスナーが働かない.
     */
    private fun createTimerModel() : TimerModel {
        return TimerModel().apply {
            onEndListener = object : TimerModel.OnEndedListener{
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
    private fun adjustedIndexOf(index : Int) : Int {
        return index % _timers.size
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
        val timerSecondsArray = Array(_timers.size) { index -> _timers[index].defaultSeconds }
        _sharedPreferences.edit().apply {
            putString(TimerConstants.PREFERENCE_PARAM_SEC_NAME, _gson.toJson(timerSecondsArray))
            putInt(TimerConstants.PREFERENCE_SAVE_VERSION_NAME, TimerConstants.PREFERENCE_SAVE_VERSION)
        }.apply()
    }

    /**
     * タイマーの秒数/個数を SharedPreferences から復元する.
     * @return 復元に成功した場合は true. 失敗した場合は false.
     */
    private fun restoreTimerPreferences() : Boolean {
        val saveVersion = _sharedPreferences.getInt(TimerConstants.PREFERENCE_SAVE_VERSION_NAME, TimerConstants.PREFERENCE_SAVE_VERSION_INVALID)
        if (saveVersion == TimerConstants.PREFERENCE_SAVE_VERSION) {
            val timerSecondsJsonString = _sharedPreferences.getString(TimerConstants.PREFERENCE_PARAM_SEC_NAME, null)
            timerSecondsJsonString?.let { json ->
                _timers.clear()
                val secondsArray = _gson.fromJson(json, Array<Float>::class.java)
                secondsArray.forEach {
                    val timerModel = createTimerModel()
                    timerModel.setSeconds(it)
                    _timers.add(timerModel)
                }
                return true
            }
        }
        return false
    }
}
