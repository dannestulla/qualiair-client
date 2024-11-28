package br.gohan.qualiar.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import br.gohan.qualiar.MainViewModel
import br.gohan.qualiar.ui.components.PollutionComponent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.max


@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
) {
    val state = viewModel.uiState.collectAsState().value

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().animateContentSize(
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        when (state) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }

            is UiState.Error -> {
                Text(state.message)
            }

            is UiState.Success -> {
                MainScreenStateless(
                    state.meterState,
                    state.iaOutput
                )
            }
        }
    }
}

@Composable
private fun MainScreenStateless(state: MeterState, iaOutput: String?) {
    val iaCardsTexts = iaOutput?.split(".")?.minus("\n")
    val animation = remember { Animatable(0f) }
    val maxMeterValue = remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(animation.value) {
        maxMeterValue.floatValue = max(maxMeterValue.floatValue, animation.value * 100f)
    }

    PollutionComponent(state = animation.toUiState(maxMeterValue.floatValue, state)) {
        coroutineScope.launch {
            animation.animateTo(
                state.maxMeterValue,
                animationSpec = spring(
                    stiffness = 40f,
                    dampingRatio = Spring.DampingRatioLowBouncy
                )
            )
        }
    }
    if (!iaCardsTexts.isNullOrEmpty()) {
        LazyColumn {
            items(iaCardsTexts.size) { index ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .animateItem(
                            fadeInSpec = tween(durationMillis = 500)
                        )
                ) {
                    Text(iaCardsTexts[index].trim(), modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
