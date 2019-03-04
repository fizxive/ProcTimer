package com.imaginaryrhombus.proctimer

import android.app.Application
import com.imaginaryrhombus.proctimer.application.TimerComponentInterface
import com.imaginaryrhombus.proctimer.application.TimerRemoteConfigClient
import com.imaginaryrhombus.proctimer.application.TimerRemoteConfigClientInterface
import com.imaginaryrhombus.proctimer.application.TimerSharedPreferencesComponent
import com.imaginaryrhombus.proctimer.ui.timer.TimerViewModel
import org.koin.android.ext.android.startKoin
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

class TestTimerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(
            timerComponentModule,
            timerViewModelModule
        ))
    }

    private val timerComponentModule = module {
        single { TestTimerComponent() as TimerComponentInterface }
        single { TimerSharedPreferencesComponent(get())}
        single { TimerRemoteConfigClient() as TimerRemoteConfigClientInterface }
    }

    private val timerViewModelModule = module {
        viewModel { TimerViewModel(get()) }
    }
}
