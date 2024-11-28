package br.gohan.qualiar.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.gohan.qualiar.ui.MeterState

@Composable
fun PollutionComponent(state: MeterState, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        CircularIndicator(state.arcValue, 240f)
        CenterTextsIndicator(
            pollutionIndex = state.polutionText.toString(),
            qualityLevel = state.description
        )
        StartButton(!state.inProgress, onClick)
    }
}