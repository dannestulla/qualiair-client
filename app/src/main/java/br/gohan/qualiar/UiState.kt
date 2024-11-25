package br.gohan.qualiar

import br.gohan.qualiar.data.AirQualityLevel

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState {

    data object Initial : UiState

    data object Loading : UiState

    data class SuccessBackend(val outputText: AirQualityLevel) : UiState

    data class SuccessAI(val outputText: String) : UiState

    data class Error(val errorMessage: Exception) : UiState
}