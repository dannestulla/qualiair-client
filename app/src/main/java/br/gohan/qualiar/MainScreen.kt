package br.gohan.qualiar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val state = viewModel.uiState.collectAsState().value
    LaunchedEffect(Unit) {
        viewModel.startNotificationService()
    }

    var pollutionMeterState by remember { mutableStateOf(MeterState()) }
    LaunchedEffect(state) {
        pollutionMeterState = if (state is UiState.SuccessBackend) {
            viewModel.sendPrompt(state.airQualityLevel.toString())

            MeterState(
                maxMeterValue = state.airQualityLevel.indice.toFloat().div(100).div(2),
                polutionText = state.airQualityLevel.indice.toFloat().div(100)
            )
        } else {
            MeterState(
                maxMeterValue = 0.0F
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PollutionMeter(pollutionMeterState)
        if (state is UiState.SuccessAI) {
            Text(state.outputText)
        }
    }
}