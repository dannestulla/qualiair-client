package br.gohan.qualiar.di

import android.content.Context
import br.gohan.qualiar.MainViewModel
import br.gohan.qualiar.baseUrl
import br.gohan.qualiar.data.MainRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::MainViewModel)
    singleOf(::MainRepository)
}

val sharedPreferences = module {
    single {
        androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }
}

val api = module {
    single {
        HttpClient(OkHttp) {
            install(Logging)
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

