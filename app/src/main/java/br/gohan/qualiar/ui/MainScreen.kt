package br.gohan.qualiar.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gohan.qualiar.MainViewModel
import br.gohan.qualiar.helpers.Location
import br.gohan.qualiar.ui.components.PollutionComponent
import kotlinx.coroutines.launch
import kotlin.math.max


@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val state = viewModel.uiState.collectAsState().value

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        when (state) {
            is UiState.Loading -> {
                Text(
                    "Gerando índice de poluição \n " +
                            "da sua localização", textAlign = TextAlign.Center, fontSize = 20.sp
                )
                Spacer(Modifier.height(30.dp))
                CircularProgressIndicator()
            }

            is UiState.Error -> {
                Text(state.message)
                Spacer(Modifier.height(10.dp))
                Button({
                    state.retry.invoke()
                }) {
                    Text("Tentar novamente")
                }
            }

            is UiState.Success -> {
                with(state) {
                    MainScreenStateless(
                        meterState,
                        if (meterState.started) iaOutput else null,
                        location
                    ) {
                        viewModel.startedMeter()
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScreenStateless(
    state: MeterState,
    iaOutput: String?,
    location: Location?,
    onStartClick: () -> Unit
) {
    val iaCardsTexts = iaOutput?.split(".")?.minus("\n")

    val animation = remember { Animatable(0f) }
    val maxMeterValue = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(animation.value) {
        maxMeterValue.floatValue = max(maxMeterValue.floatValue, animation.value * 100f)
    }
    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier.animateContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        location?.let { loc ->
            loc.city?.uppercase()?.let { city -> Text(city, fontSize = 20.sp) }
            loc.country?.uppercase()?.let { country -> Text(country, fontSize = 20.sp) }
        }
        PollutionComponent(animation.toUiState(maxMeterValue.floatValue, state)) {
            coroutineScope.launch {
                animation.animateTo(
                    state.maxMeterValue,
                    animationSpec = spring(
                        stiffness = 20f,
                        dampingRatio = Spring.DampingRatioLowBouncy
                    )
                )
                onStartClick.invoke()
            }
        }
        if (!iaCardsTexts.isNullOrEmpty()) {
            LazyColumn {
                items(iaCardsTexts.size) { index ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(iaCardsTexts[index].trim(), modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
