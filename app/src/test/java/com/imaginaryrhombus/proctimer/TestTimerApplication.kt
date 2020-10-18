package com.imaginaryrhombus.proctimer

import android.app.Application
import com.imaginaryrhombus.proctimer.application.TimerComponent
import com.imaginaryrhombus.proctimer.application.TimerComponentInterface
import com.imaginaryrhombus.proctimer.application.TimerRemoteConfigClientInterface
import com.imaginaryrhombus.proctimer.application.TimerSharedPreferencesComponent
import com.imaginaryrhombus.proctimer.ui.timer.TimerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class TestTimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@TestTimerApplication)
            modules(timerComponentModule, timerViewModelModule)
        }
    }

    private val timerComponentModule = module {
        single<TimerComponentInterface> { TimerComponent(androidApplication()) }
        single { TimerSharedPreferencesComponent(get()) }
        single<TimerRemoteConfigClientInterface> { StabRemoteConfigClient() }
    }

    private val timerViewModelModule = module {
        viewModel { TimerViewModel(get()) }
    }
}
