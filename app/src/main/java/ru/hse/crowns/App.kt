package ru.hse.crowns

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.hse.crowns.di.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            modules(appModule, viewModelsModule, dataStoreModule)
        }
    }
}