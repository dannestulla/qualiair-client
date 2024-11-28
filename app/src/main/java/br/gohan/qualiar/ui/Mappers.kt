package br.gohan.qualiar.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import br.gohan.qualiar.data.NetworkState


fun NetworkState.SuccessBackend.toUiState(): MeterState {
    this.response.let {
        return MeterState(
            maxMeterValue = it.indice.toFloat().div(100).div(2),
            polutionText = it.indice.toFloat(),
            description = it.descricao
        )
    }
}

fun NetworkState.SuccessAI.toUiState(uiState: UiState): UiState {
    return if (uiState is UiState.Success) {
        UiState.Success(uiState.meterState, this.outputText)
    } else {
        UiState.Error("Fluxo das chamadas com problema")
    }
}

fun Animatable<Float, AnimationVector1D>.toUiState(maxSpeed: Float, meterState: MeterState) =
    MeterState(
        arcValue = value,
        maxMeterValue = maxSpeed,
        inProgress = isRunning,
        polutionText = meterState.polutionText,
        description = meterState.description
    )

