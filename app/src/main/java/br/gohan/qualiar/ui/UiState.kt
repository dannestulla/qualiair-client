package br.gohan.qualiar.ui

sealed interface UiState {

    data object Loading : UiState

    data class Error(val message: String) : UiState

    data class Success(val meterState: MeterState, val iaOutput: String? = null) : UiState
}