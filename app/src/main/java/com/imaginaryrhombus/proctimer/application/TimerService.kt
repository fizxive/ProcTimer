package com.imaginaryrhombus.proctimer.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.imaginaryrhombus.proctimer.R

/**
 * バックグラウンド動作のためのサービス.
 *
 * 通知の表示と、表示内容の監視を行う.
 */
class TimerService : LifecycleService() {

    class TimerServiceBinder(val service: TimerService) : Binder()

    private val binder = TimerServiceBinder(this)

    private val notificationId = 97101

    private lateinit var notificationCompatBuilder: NotificationCompat.Builder

    private lateinit var notificationManager: NotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notificationChannelId = "current_timer_notification"
        val notificationName = getString(R.string.app_name)
        val notificationDescription = "現在のタイマー"

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var mChannel = notificationManager.getNotificationChannel(notificationChannelId)
            if (mChannel == null) {
                mChannel = NotificationChannel(
                    notificationChannelId,
                    notificationName, NotificationManager.IMPORTANCE_DEFAULT
                )
                mChannel.description = notificationDescription
            }
            notificationManager.createNotificationChannel(mChannel)
        }

        notificationCompatBuilder = NotificationCompat.Builder(this, notificationChannelId).apply {
            setContentTitle(notificationName)
            setContentText("")
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setOnlyAlertOnce(true)
        }

        startForeground(notificationId, notificationCompatBuilder.build())

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return binder
    }

    /**
     * 通知内容に表示する文字列 LiveData の設定.
     */
    fun setStringLiveData(liveData: LiveData<String>) {
        liveData.observe(this, Observer {
            notificationCompatBuilder.setContentText(it)
            notificationManager.notify(notificationId, notificationCompatBuilder.build())
        })
    }
}
