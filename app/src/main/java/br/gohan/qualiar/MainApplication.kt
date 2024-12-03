package br.gohan.qualiar

import android.app.Application
import br.gohan.qualiar.di.api
import br.gohan.qualiar.di.appModule
import br.gohan.qualiar.di.sharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(api, appModule, sharedPreferences)
        }
    }
}