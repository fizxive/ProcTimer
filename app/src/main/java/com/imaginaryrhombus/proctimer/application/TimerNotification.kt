package com.imaginaryrhombus.proctimer.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import com.imaginaryrhombus.proctimer.R
import com.imaginaryrhombus.proctimer.TimerActivity

/**
 * タイマーが動作している時に表示する通知の管理クラス.
 * 必要に応じて、汎化する.
 */
class TimerNotification(context: Context) {

    /**
     * 通知に使用するID.
     */
    private val notificationId = "timer_notification_id"

    /**
     * 通知に使用する番号.
     */
    private val notificationTagId = 97

    /**
     * 通知管理のマネージャー.
     */
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * 互換性の高い通知を作るためのビルダー.
     */
    private val notificationCompatBuilder =
        NotificationCompat.Builder(context, notificationId).apply {
            setContentTitle(context.getString(R.string.app_name))
            setContentText("")
            setSmallIcon(R.mipmap.ic_launcher)

            val intent = Intent(context, TimerActivity::class.java)
            val pendingIntent = PendingIntent
                .getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            setContentIntent(pendingIntent)
    }

    /**
     * LiveData に対して設定する監視.
     */
    private val timerObserver : (Float) -> Unit = { seconds : Float ->
        notificationCompatBuilder.setContentText(seconds.toString())
        if (isVisible) {
            notifyInternal()
        }
    }

    /**
     * 通知が見えているかどうか.
     */
    private var isVisible = false

    init {
        // Android O から通知チャンネルの設定が必須になるが、旧バージョンでは使用できないための措置.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationId,
                "タイマー動作中通知",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    /**
     * 通知を表示、更新する.
     * @param liveData 通知に表示するデータ.
     */
    fun open(liveData: LiveData<Float>){
        notifyInternal()
        liveData.observeForever(timerObserver)
        isVisible = true
    }

    /**
     * 通知を閉じる
     * @param liveData 通知に表示していたデータ.
     */
    fun close(liveData: LiveData<Float>){
        cancelInternal()
        liveData.removeObserver(timerObserver)
        isVisible = false
    }

    /**
     * 通知を送信する(内部用).
     */
    private fun notifyInternal() {
        notificationManager.notify(notificationTagId, notificationCompatBuilder.build())
    }

    /**
     * 通知をキャンセルする(内部用).
     */
    private fun cancelInternal() {
        notificationManager.cancel(notificationTagId)
    }
}
