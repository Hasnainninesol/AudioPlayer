package com.example.audioplayer.appclass

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.audioplayer.CHANNEL
import com.example.audioplayer.repository.AudioRepo
import com.example.audioplayer.viewmodel.ViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
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
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL,
                "SongChannel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "SongChannel"
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)
        }
    }

}