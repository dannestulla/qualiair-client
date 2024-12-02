package br.gohan.qualiar.data

import android.content.SharedPreferences
import br.gohan.qualiar.BuildConfig
import br.gohan.qualiar.helpers.Location
import br.gohan.qualiar.helpers.LocationHelper.Companion.LOCATION
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

class MainRepository(
    private val httpClient: HttpClient,
    private val sharedPreferences: SharedPreferences
) {
    lateinit var token: String

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
        handleRequest(_networkState) {
            httpClient.post(notificationsEndpoint) {
                setBody(token)
            }
        }
    }

    suspend fun generateIndex(location: Location) {
        handleRequest(_networkState) {
            httpClient.get(generateIndexEndpoint) {
                headers.append("token", token)
                url {
                    parameters.append("latitude", location.latitude.toString())
                    parameters.append("longitude", location.longitude.toString())
                }
            }
            // this should be at Location Helper
            sharedPreferences.edit().putString(
                LOCATION,
                location.city
            ).apply()
        }
    }

    suspend fun getAirQualityLevel(): AirQualityLevel? {
        return handleRequest(_networkState) {
            val response = httpClient.get(airQualityLevel) {
                headers.append("token", token)
            }.body<AirQualityLevel>()
            response
        }
    }

    suspend fun sendIaPrompt(promptIA: String) {
        handleRequest(_networkState) {
            model.generateContent(
                content {
                    text(promptIA)
                }
            )
        }
    }

    companion object {
        const val notificationsEndpoint = "/notifications/save-token"
        const val generateIndexEndpoint = "/qualidade-ar/gerar-dados"
        const val airQualityLevel = "/qualidade-ar/calculado"
        const val TAG = "MainRepository"
    }
}