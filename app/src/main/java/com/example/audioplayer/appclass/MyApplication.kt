package com.example.audioplayer.appclass

import android.app.Application
import com.example.audioplayer.viewmodel.ViewModel
import com.example.audioplayer.repository.AudioRepo
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            androidLogger(Level.ERROR)
            modules(listOf(appModule))
        }
    }

    private val appModule = module {
        single { AudioRepo(this@MyApplication) }
        single {
            ViewModel(
                this@MyApplication,
                AudioRepo(this@MyApplication)
            )
        }
    }

}