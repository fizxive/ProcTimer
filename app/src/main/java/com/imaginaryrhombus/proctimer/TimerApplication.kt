package com.imaginaryrhombus.proctimer

import android.app.Application
import com.imaginaryrhombus.proctimer.application.TimerComponent
import com.imaginaryrhombus.proctimer.application.TimerComponentInterface
import com.imaginaryrhombus.proctimer.application.TimerRemoteConfigClient
import com.imaginaryrhombus.proctimer.application.TimerSharedPreferencesComponent
import com.imaginaryrhombus.proctimer.ui.timer.TimerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class TimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            modules(timerComponentModule, timerViewModelModule)
        }
    }

    private val timerComponentModule = module {
        single<TimerComponentInterface> { TimerComponent(androidApplication()) }
        single { TimerSharedPreferencesComponent(get()) }
        single { TimerRemoteConfigClient() }
    }

    private val timerViewModelModule = module {
        viewModel { TimerViewModel(get()) }
    }
}
