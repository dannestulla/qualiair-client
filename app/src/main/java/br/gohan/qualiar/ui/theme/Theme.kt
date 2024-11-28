package br.gohan.qualiar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorPalette = darkColorScheme(
    primary = Purple700,
    secondary = Teal200,
    background = DarkColor2,
    surface = DarkColor2,
    onSurface = LightColor2,
    onBackground = LightColor2
)

private val ColorPalette2 = darkColorScheme(
    primary = Purple700,      // Cor principal (usada para elementos primários, como botões)
    onPrimary = LightColor,   // Cor usada em textos ou ícones sobre a cor primária

    secondary = Teal200,      // Cor secundária (usada para destacar elementos menos importantes)
    onSecondary = DarkColor,  // Cor usada em textos ou ícones sobre a cor secundária

    background = DarkColor2,  // Cor do fundo principal da aplicação
    onBackground = LightColor2, // Cor para textos ou elementos sobre o fundo principal

    surface = DarkColor,      // Cor para superfícies como cartões ou pop-ups
    onSurface = LightColor,   // Cor para textos ou ícones sobre as superfícies

    error = Pink,             // Cor para mensagens ou elementos de erro
    onError = LightColor2     // Cor usada em textos ou ícones sobre a cor de erro
)

@Composable
fun QualiArTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        //colorScheme = ColorPalette2,
        //typography = Typography,
        shapes = Shapes,
        content = content
    )
}