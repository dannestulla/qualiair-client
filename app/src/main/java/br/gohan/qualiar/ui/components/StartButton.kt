package br.gohan.qualiar.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StartButton(isEnabled: Boolean, isVisible: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(isVisible) {
        Button(
            onClick = onClick,
            modifier = Modifier.padding(bottom = 24.dp),
            enabled = isEnabled,
        ) {
            Text(text = "START", modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
        }
    }
}
