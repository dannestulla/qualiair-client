package br.gohan.qualiar.ui

import br.gohan.qualiar.helpers.Location

sealed interface UiState {

    data class Loading(val city: String? = null) : UiState

    data class Error(val message: String, val retry: () -> Unit) : UiState

    data class Success(
        val meterState: MeterState,
        val iaOutput: String? = null,
        val location: Location?
    ) : UiState
}