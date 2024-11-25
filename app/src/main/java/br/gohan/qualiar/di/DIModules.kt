package br.gohan.qualiar.di

import br.gohan.qualiar.data.MainRepository
import br.gohan.qualiar.MainViewModel
import com.google.ai.client.generativeai.GenerativeModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::MainViewModel)
    factoryOf(::MainRepository)
}

val api = module {
    single {
        HttpClient(OkHttp) {
            install(Logging)
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(HttpTimeout)
        }
    }
}

