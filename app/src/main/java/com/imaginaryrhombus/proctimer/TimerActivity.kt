package com.imaginaryrhombus.proctimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.imaginaryrhombus.proctimer.ui.timer.TimerFragment

class TimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timer_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TimerFragment.newInstance())
                .commitNow()
        }
    }
}
