package br.gohan.qualiar.di

import android.content.Context
import android.util.Log
import br.gohan.qualiar.MainViewModel
import br.gohan.qualiar.baseUrl
import br.gohan.qualiar.data.MainRepository
import br.gohan.qualiar.helpers.Location
import com.google.firebase.messaging.FirebaseMessaging
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { MainRepository(get(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
}

val sharedPreferences = module {
    single {
        androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }
}

val api = module {
    single {
        HttpClient(OkHttp) {
            expectSuccess = true
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(HttpTimeout)
            defaultRequest {
                url(baseUrl)
            }
        }
    }
}

