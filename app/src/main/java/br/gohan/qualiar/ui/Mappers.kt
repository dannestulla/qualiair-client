package br.gohan.qualiar.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import br.gohan.qualiar.data.NetworkState
import kotlin.math.roundToInt


fun NetworkState.SuccessBackend.toUiState(): MeterState {
    this.response.let {
        return MeterState(
            maxMeterValue = it.indice.toFloat().div(100).div(2),
            polutionText = it.indice,
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

fun Animatable<Float, AnimationVector1D>.toUiState(maxValue: Float, meterState: MeterState) =
    MeterState(
        arcValue = value,
        maxMeterValue = maxValue,
        inProgress = isRunning,
        polutionText = value.times(100).roundToInt().times(2),
        description = meterState.description
    )

