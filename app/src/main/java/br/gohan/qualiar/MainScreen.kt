package br.gohan.qualiar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            viewModel.startNotificationService()
        }) {
            Text(
                "Start",
                fontSize = 25.sp
            )
        }
        Button(onClick = {
            viewModel.getAirPollution()
        }) {
            Text(
                "generate index",
                fontSize = 25.sp
            )
        }
        Button(onClick = {
            viewModel.getAirPollution()
        }) {
            Text(
                "generate index",
                fontSize = 25.sp
            )
        }

        Button(
            onClick = {
                if (state is UiState.SuccessBackend) {
                    viewModel.sendPrompt(state.outputText.toString())
                }
            }) {
            Text(
                "AI fetch",
                fontSize = 25.sp
            )
        }
        when (state) {
            is UiState.SuccessBackend -> {
                Text(state.outputText.toString())
            }
            is UiState.Error -> Text(state.errorMessage.toString())
            is UiState.Initial -> CircularProgressIndicator()
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.SuccessAI -> Text(state.outputText)
        }
    }
}