package br.gohan.qualiar.data

import com.google.ai.client.generativeai.type.GenerateContentResponse
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow

suspend fun <T> handleRequest(
    networkState: MutableStateFlow<NetworkState>,
    defaultValue: T? = null,
    request: suspend () -> T
): T? {
    return try {
        val result = request()
        when (result) {
            is AirQualityLevel -> networkState.emit(NetworkState.SuccessBackend(result))
            is GenerateContentResponse -> networkState.emit(
                NetworkState.SuccessAI(
                    result.text ?: ""
                )
            )
        }
        result
    } catch (e: ClientRequestException) {
        networkState.emit(NetworkState.Error("Client error: ${e.message}"))
        logError(e)
        defaultValue
    } catch (e: ServerResponseException) {
        networkState.emit(NetworkState.Error("Server error: ${e.message}"))
        logError(e)
        defaultValue
    } catch (e: RedirectResponseException) {
        networkState.emit(NetworkState.Error("Redirect error: ${e.message}"))
        logError(e)
        defaultValue
    } catch (e: TimeoutCancellationException) {
        networkState.emit(NetworkState.Error("Timeout: ${e.message}"))
        logError(e)
        defaultValue
    } catch (e: Exception) {
        networkState.emit(NetworkState.Error("Unexpected error: ${e.message}"))
        logError(e)
        defaultValue
    }
}

fun logError(e: Exception) {
    // Função para registrar os erros, pode ser usando um logger ou print
    println("Error: ${e.message}")
}
