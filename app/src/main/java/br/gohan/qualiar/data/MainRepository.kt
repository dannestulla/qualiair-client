package br.gohan.qualiar.data

import br.gohan.qualiar.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainRepository(
    private val httpClient: HttpClient,
) {
    private lateinit var token: String

    private val _networkState: MutableStateFlow<NetworkState> =
        MutableStateFlow(NetworkState.Initial)
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    private val model =
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.API_KEY
        )

    suspend fun saveToken(token: String) {
        this.token = token
        try {
            httpClient.post("${BuildConfig.BASE_URL}$notificationsEndpoint") {
                setBody(token)
            }
        } catch (e: Exception) {
            _networkState.update {
                NetworkState.Error(Exception(e.localizedMessage ?: ""))
            }
        }
    }

    suspend fun getAirQualityLevel(): AirQualityLevel? {
        try {
            val response = httpClient.get("${BuildConfig.BASE_URL}$airQualityLevel") {
                headers.append("token", token)
            }.body<AirQualityLevel>()
            _networkState.update {
                NetworkState.SuccessBackend(response)
            }
            return response
        } catch (error: Exception) {
            _networkState.update {
                NetworkState.Error(error)
            }
            return null
        }
    }

    suspend fun sendIaPrompt(promptIA: String) {
        try {
            val response = model.generateContent(
                content {
                    text(promptIA)
                }
            )
            _networkState.update {
                NetworkState.SuccessAI(response.text!!, false)
            }
        } catch (e: Exception) {
            _networkState.update {
                NetworkState.Error(Exception(e.localizedMessage ?: ""))
            }
        }
    }

    companion object {
        private val notificationsEndpoint = "/notifications/save-token"
        private val airQualityLevel = "/qualidade-ar/calculado"
        private val TAG = "MainRepository"
    }
}