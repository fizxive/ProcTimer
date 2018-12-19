package com.imaginaryrhombus.proctimer.ui.timer

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import java.util.*

/**
 * タイマー用の ViewModel.
 */
class TimerViewModel : ViewModel() {

    /// タイマー本体.(外部からの直接設定をしないようにする)
    private var timer = TimerModel()

    /// タイマーのテキスト化したときの表記.
    var timerText = MutableLiveData<String>()
    private set

    /// 時間経過の判定を行う間隔.
    val tickInterval = 10L

    /// 時間経過用のハンドラ.
    val tickHandler = Handler()

    /**
     * 時間経過を司る内部クラス
     * TODO : 別ファイルにする. テストしやすくするため.
     */
    class TimeTicker {

        private var prevMilliseconds = 0L

        var latestTick = 0.0f
        private set

        init {
            setPrevious()
        }

        fun tick() {
            val currentMilliseconds = Calendar.getInstance(Locale.getDefault()).timeInMillis
            val deltaMilliseconds = currentMilliseconds - prevMilliseconds

            latestTick = deltaMilliseconds / 1000.0f
            prevMilliseconds = currentMilliseconds

            setPrevious()
        }

        fun setPrevious() {
            prevMilliseconds = Calendar.getInstance(Locale.getDefault()).timeInMillis
        }
    }

    private var timeTicker = TimeTicker()

    /// 時間経過用のワーカー.
    private val tickRunner = object : Runnable {

        override fun run() {

            timeTicker.tick()

            timer.tick(timeTicker.latestTick)

            updateText()

            tickHandler.postDelayed(this, tickInterval)
        }
    }

    init {
        startTick()
    }

    fun startTick() {
        timeTicker.setPrevious()
        tickHandler.post(tickRunner)
    }

    fun stopTick() {
        tickHandler.removeCallbacks(tickRunner)
    }

    fun updateText() {
        val timerSecondsInt = timer.seconds.toInt()
        val minutes = timerSecondsInt / 60
        val seconds = timerSecondsInt % 60
        timerText.postValue("%02d:%02d".format(minutes, seconds))
    }
}
