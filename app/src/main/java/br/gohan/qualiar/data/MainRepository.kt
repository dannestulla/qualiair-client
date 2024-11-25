package br.gohan.qualiar.data

import android.util.Log
import br.gohan.qualiar.UiState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class MainRepository(
    private val httpClient: HttpClient,
) {
    private val baseUrl =
        "https://b8af-201-37-127-223.ngrok-free.app"
    private val notificationsEndpoint = "/notifications/save-token"
    private val fetchOpenWeatherApi = "/qualidade-ar/gerar-dados"
    private val generateIndex = "/qualidade-ar/calculado"
    private val TAG = "MainRepository"

    suspend fun saveToken(token: String) {
        try {
            httpClient.post("$baseUrl$notificationsEndpoint") {
                setBody(token)
            }
        } catch (error: Exception) {
            Log.e(TAG, "saveToken: $error")
        }
    }

    suspend fun fetchOpenWeatherData(token: String, latitude: Double, longitude: Double) {
        // server gets data in open weather and saves in db, however client should not know about this
    }

    suspend fun getAirPollutionData(
        token: String,
    ): UiState {
        return try {
            val response = httpClient.get("$baseUrl$generateIndex") {
                headers.append("token", token)
            }.body<AirQualityLevel>()
            UiState.SuccessBackend(response)
        } catch (error : Exception) {
            Log.e(TAG, "getAirPollutionData error $error")
            UiState.Error(error)
        }
    }
}