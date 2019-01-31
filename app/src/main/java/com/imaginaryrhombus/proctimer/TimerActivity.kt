package com.imaginaryrhombus.proctimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.imaginaryrhombus.proctimer.ui.timer.TimerFragment

class TimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timer_activity)

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val defaultValueMap = HashMap<String, Any>().apply {
            put("LEAST_VERSION", "0.0.0.0")
        }
        remoteConfig.setDefaults(defaultValueMap)
        remoteConfig.fetch().addOnCompleteListener {
            remoteConfig.activateFetched()
            Toast.makeText(this, "Remote Config fetch complete,\ncurrent least version is ${remoteConfig.getString("LEAST_VERSION")}.", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Remote Config fetch failure.", Toast.LENGTH_LONG).show()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TimerFragment.newInstance())
                .commitNow()
        }
    }
}
