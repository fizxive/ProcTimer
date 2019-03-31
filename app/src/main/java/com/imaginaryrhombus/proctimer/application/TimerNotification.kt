package com.imaginaryrhombus.proctimer.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
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
            setSmallIcon(R.mipmap.ic_launcher)
    }

    /**
     * 通知がクリックされたらタイマー画面に飛ばすための Intent.
     */
    private val activityIntent = Intent(context, TimerActivity::class.java)

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
     * 通知のタイトルを設定する.
     */
    fun setTitle(title: String): TimerNotification {
        notificationCompatBuilder.setContentTitle(title)
        return this
    }

    /**
     * 通知の内容を設定する.
     */
    fun setText(text: String): TimerNotification {
        notificationCompatBuilder.setContentText(text)
        return this
    }

    /**
     * 通知を表示、更新する.
     */
    fun show(){
        notificationManager.notify(notificationTagId, notificationCompatBuilder.build())
    }

    /**
     * 通知を閉じる
     */
    fun close(){
        notificationManager.cancel(notificationTagId)
    }
}
