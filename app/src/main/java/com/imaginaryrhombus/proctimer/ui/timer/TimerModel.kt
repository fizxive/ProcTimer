package com.imaginaryrhombus.proctimer.ui.timer

/**
 * 一つ一つのタイマー用モデル.
 * @param seconds 残り秒数.
 */
class TimerModel(var seconds: Float) {

    /// タイマー表記の文字列.
    var text : String = formatSeconds(seconds)

    /**
     * 時間を経過させる.
     * @param deltaSeconds 経過時間.
     */
    fun tick(deltaSeconds :Float) {
        val nextSeconds = seconds - deltaSeconds;
        seconds = if(nextSeconds > 0.0f) nextSeconds else 0.0f
        text = formatSeconds(seconds)
    }

    /**
     * 現在のタイマーが終了しているか.
     */
    fun isEnded() {seconds <= 0.0f}

    /**
     * 内部の秒数をテキストに変換する.
     */
    private fun formatSeconds(seconds: Float):String {
        val minutesInt = seconds.toInt() / 60
        val secondsInt = seconds.toInt() % 60
        return "%02d:%02d".format(minutesInt, secondsInt)
    }
}