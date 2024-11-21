package br.gohan.qualiar

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class MainRepository(
    private val httpClient: HttpClient,
) {
    private val baseUrl = "http://192.168.0.67:8080/notifications"
    private val TAG = "MainRepository"

    suspend fun sendToken(token: String) {
        val response = httpClient.post("$baseUrl/save-token") {
            setBody(token)
        }
        Log.i(TAG, "sendToken: ${response.status}")
    }
}