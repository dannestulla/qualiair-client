package br.gohan.qualiar.data

import android.content.SharedPreferences
import android.util.Log
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
import kotlinx.coroutines.flow.update

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
        try {
            httpClient.post(notificationsEndpoint) {
                setBody(token)
            }
        } catch (e: Exception) {
            _networkState.update {
                NetworkState.Error(Exception(e.localizedMessage ?: ""))
            }
        }
    }

    suspend fun generateIndex(location: Location) {
        try {
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
        } catch (error: Exception) {
            Log.e(TAG, "saveToken: $error")
        }
    }

    suspend fun getAirQualityLevel(): AirQualityLevel? {
        try {
            val response = httpClient.get(airQualityLevel) {
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
                NetworkState.SuccessAI(response.text!!)
            }
        } catch (e: Exception) {
            _networkState.update {
                NetworkState.Error(Exception(e.localizedMessage ?: ""))
            }
        }
    }

    companion object {
        const val notificationsEndpoint = "/notifications/save-token"
        const val generateIndexEndpoint = "/qualidade-ar/gerar-dados"
        const val airQualityLevel = "/qualidade-ar/calculado"
        const val TAG = "MainRepository"
    }
}