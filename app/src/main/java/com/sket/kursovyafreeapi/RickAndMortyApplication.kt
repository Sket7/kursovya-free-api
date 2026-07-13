package com.sket.kursovyafreeapi

import android.app.Application
import com.sket.kursovyafreeapi.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin

class RickAndMortyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            logger(AndroidLogger())
            androidContext(this@RickAndMortyApplication)
            modules(appModules)
        }
    }
}
