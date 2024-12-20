package br.gohan.qualiar.data


sealed class NetworkState {
    data object Initial : NetworkState()

    data class SuccessBackend(val response: AirQualityLevel) : NetworkState()

    data class SuccessAI(val outputText: String) : NetworkState()

    data class Error(val errorMessage: String) : NetworkState()
}

