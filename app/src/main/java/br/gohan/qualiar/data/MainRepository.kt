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
        "https://f906-2804-14c-7980-9a02-7d29-7bd2-b7f6-8f70.ngrok-free.app"
    private val notificationsEndpoint = "/notifications/save-token"
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