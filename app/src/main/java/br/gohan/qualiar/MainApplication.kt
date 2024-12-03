package br.gohan.qualiar

import android.app.Application
import br.gohan.qualiar.di.api
import br.gohan.qualiar.di.appModule
import br.gohan.qualiar.di.sharedPreferences
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val firebaseToken = runBlocking { getFirebaseToken() }

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(api, appModule, sharedPreferences)
        }
    }

    private suspend fun getFirebaseToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            null // Trate erros caso a obtenção do token falhe
        }
    }
}