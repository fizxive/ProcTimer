package com.imaginaryrhombus.proctimer.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Message
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import com.imaginaryrhombus.proctimer.R
import com.imaginaryrhombus.proctimer.TimerActivity

/**
 * タイマーが動作終了した後の通知.
 */
class TimerEndNotification(context: Context) {

    /**
     * 通知に使用するID.
     */
    private val notificationId = "timer_notification_id"

    /**
     * 通知に使用する番号.
     */
    private val notificationTagId = 98

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
            setAutoCancel(true)

            val intent = Intent(context, TimerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent
                .getActivity(context, 0, intent, 0)

            setContentIntent(pendingIntent)
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
     * @param message 通知に表示するメッセージ.
     */
    fun open(message: String){
        notificationCompatBuilder.setContentText(message)
        notificationManager.notify(notificationTagId, notificationCompatBuilder.build())
    }

    /**
     * 通知を閉じる
     */
    fun close(){
        notificationManager.cancel(notificationTagId)
    }
}
