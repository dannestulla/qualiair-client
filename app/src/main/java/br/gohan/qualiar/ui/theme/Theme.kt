package br.gohan.qualiar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorPalette = darkColorScheme(
    primary = Purple700,
    secondary = Teal200,
    background = DarkColor2,
    surface = DarkColor2,
    onSurface = LightColor2,
    onBackground = LightColor2
)

@Composable
fun QualiArTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = ColorPalette,
        //typography = Typography,
        shapes = Shapes,
        content = content
    )
}