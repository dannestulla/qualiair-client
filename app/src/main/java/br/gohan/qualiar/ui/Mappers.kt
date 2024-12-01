package br.gohan.qualiar.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import br.gohan.qualiar.data.NetworkState
import br.gohan.qualiar.helpers.LocationHelper
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
        UiState.Success(
            uiState.meterState.copy(enableButton = true),
            this.outputText,
            LocationHelper.currentLocation.value,
        )
    } else {
        UiState.Error("Fluxo das chamadas com problema") {
        }
    }
}

fun Animatable<Float, AnimationVector1D>.toUiState(maxValue: Float, meterState: MeterState) =
    MeterState(
        arcValue = value,
        maxMeterValue = maxValue,
        polutionText = value.times(100).roundToInt().times(2),
        description = meterState.description,
        enableButton = meterState.enableButton
    )

