package com.imaginaryrhombus.proctimer

import android.app.Application
import com.imaginaryrhombus.proctimer.application.*
import com.imaginaryrhombus.proctimer.ui.timer.TimerViewModel
import org.koin.android.ext.android.startKoin
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

class TimerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(
            timerComponentModule,
            timerViewModelModule
        ))
    }

    private val timerComponentModule = module {
        single { TimerComponent(get()) as TimerComponentInterface }
        single { TimerSharedPreferencesComponent(get()) }
        single { TimerRemoteConfigClient() as TimerRemoteConfigClientInterface}
    }

    private val timerViewModelModule = module {
        viewModel { TimerViewModel(get()) }
    }
}
