package com.imaginaryrhombus.proctimer.ui.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.imaginaryrhombus.proctimer.application.TimerSharedPreferencesComponent
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import java.util.LinkedList

/**
 * 複数のタイマーを管理する Model.
 */
class MultiTimerModel(
    private val timerSharedPreferencesComponent: TimerSharedPreferencesComponent
) : TimerModel.OnEndedListener {

    /**
     * タイマー切り替わり時のリスナーインターフェース.
     */
    interface OnTimerChangedListener {
        /**
         * タイマーが切り替わるときに呼ばれる.
         */
        fun onTimerChanged()

        /**
         * タイマーが追加されたときに呼ばれる.
         */
        fun onTimerAdded()

        /**
         * タイマーが破棄されたときに呼ばれる.
         */
        fun onTimerRemoved()
    }

    /**
     * 現在の動作しているタイマーを取得する.
     * これを監視して switchMap で秒を監視するLiveDataを作成するようにする.
     */
    private var _activeTimerModel = MutableLiveData<TimerModel>()

    val activeTimerModel: LiveData<TimerModel>
    get() {
        return _activeTimerModel
    }

    /**
     * 全タイマーを取得する.
     */
    val timerList: List<TimerModel>
    get() {
        return _linkedTimerList.toList()
    }

    /**
     * 全タイマーを順番に並べ替えたもの.
     */
    private var _linkedTimerList = LinkedList<TimerModel>()

    /**
     * 各タイマー終了時のリスナー.
     */
    var onEachTimerEndedListener: TimerModel.OnEndedListener? = null

    /**
     * タイマーが切り替わったときのリスナー.
     */
    var onTimerChangedListener: OnTimerChangedListener? = null

    /**
     * 現在のタイマーのデフォルト秒数を取得・設定する.
     */
    var activeTimerSeconds: Float
        get() {
            return _linkedTimerList.first.defaultSeconds
        }
        set(value) {
            _linkedTimerList.first.setSeconds(value)
            saveTimerPreferences()
        }

    /**
     * タイマーの最大数.
     */
    private val timerMax = 4

    /**
     * タイマーの最小数.
     */
    private val timerMin = 1

    /**
     * タイマーが動作中かどうか確認する.
     */
    val isWorking: LiveData<Boolean> = Transformations.switchMap(activeTimerModel) { timerModel ->
        timerModel.isWorking
    }

    init {
        restoreTimerPreferences()
        notifyActiveTimerModel()
    }

    /**
     * タイマーを末尾に追加する.
     */
    fun addTimer(onFailureListener: () -> Unit = {}) {
        if (_linkedTimerList.size < timerMax) {
            _linkedTimerList.addLast(createTimerModel())
            onTimerChangedListener?.onTimerAdded()
            saveTimerPreferences()
        } else {
            onFailureListener()
        }
    }

    /**
     * 現在のタイマーを削除する.
     * @param onFailureListener タイマーが1つのときに削除しようとしたときの動作.
     */
    fun removeCurrentTimer(onFailureListener: () -> Unit = {}) {
        if (_linkedTimerList.size > timerMin) {
            _linkedTimerList.removeFirst()
            saveTimerPreferences()
            onTimerChangedListener?.onTimerRemoved()
            notifyActiveTimerModel()
        } else {
            onFailureListener()
        }
    }

    /**
     * 現在のタイマーを動作開始させる.
     */
    fun startCurrentTimer() {
        _linkedTimerList.first.start()
    }

    /**
     * 現在のタイマーを停止させる.
     */
    fun stopCurrentTimer() {
        _linkedTimerList.first.stop()
    }

    /**
     * 現在のタイマーをリセットする.
     */
    fun resetCurrentTimer() {
        _linkedTimerList.first.reset()
    }

    /**
     * 現在のタイマーをリセットし、次のタイマーに移行する.
     */
    fun next() {
        resetCurrentTimer()
        _linkedTimerList.addLast(_linkedTimerList.removeFirst())
        onTimerChangedListener?.onTimerChanged()
        notifyActiveTimerModel()
    }

    /**
     * 設定がなされた TimerModel を作成.
     * @note この関数を通さないと終了時リスナーが働かない.
     */
    private fun createTimerModel(): TimerModel {
        return TimerModel(this).apply {
            setSeconds(TimerConstants.TIMER_DEFAULT_SECONDS)
        }
    }

    override fun onEnd() {
        onEachTimerEndedListener?.onEnd()
    }

    /**
     * タイマーの秒数/個数を SharedPreferences にセーブする.
     */
    private fun saveTimerPreferences() {
        timerSharedPreferencesComponent.timerSecondsList = List(timerList.size) {
            index -> timerList[index].defaultSeconds
        }
    }

    /**
     * タイマーの秒数/個数を SharedPreferences から復元する.
     * @return 復元に成功した場合は true. 失敗した場合は false.
     */
    private fun restoreTimerPreferences() {
        _linkedTimerList.clear()
        timerSharedPreferencesComponent.timerSecondsList.forEach {
            _linkedTimerList.addLast(createTimerModel().apply { setSeconds(it) })
        }
    }

    /**
     * アクティブなタイマーが変わったことを通知する
     */
    private fun notifyActiveTimerModel() {
        _activeTimerModel.postValue(timerList.first())
    }
}
