package br.gohan.qualiar

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import br.gohan.qualiar.ui.theme.DarkGradient
import br.gohan.qualiar.ui.theme.Green500
import br.gohan.qualiar.ui.theme.Green200
import br.gohan.qualiar.ui.theme.LightColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gohan.qualiar.ui.theme.GreenGradient
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.max

//all ui components is customizable
//screens of speed
@Composable
fun PollutionMeter(state: MeterState) {
    val coroutineScope = rememberCoroutineScope()

    val animation = remember { Animatable(0f) }
    val maxMeterValue = remember { mutableStateOf(0f) }
    maxMeterValue.value = max(maxMeterValue.value, animation.value * 100f)

    SpeedTestScreen(state = animation.toUiState(maxMeterValue.value, state.polutionText)) {
        coroutineScope.launch {
            maxMeterValue.value = 0f
            startAnimation(animation, state.maxMeterValue)
        }
    }
}
//static values for animtations
suspend fun startAnimation(animation: Animatable<Float, AnimationVector1D>, target: Float) {
    animation.animateTo(target, keyframes {
        durationMillis = 3000
        //0f at 0 with CubicBezierEasing(0f, 1.5f, 0.8f, 1f)
        target.div(2) at 2000 with LinearOutSlowInEasing
    })
}

fun Animatable<Float, AnimationVector1D>.toUiState(maxSpeed: Float, polutionText: Float) = MeterState(
    arcValue = value,
    maxMeterValue = maxSpeed,//"%.1f".format(value * 100),
    inProgress = isRunning,
    polutionText = polutionText
)

@Composable
private fun SpeedTestScreen(state: MeterState, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGradient),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        PolluitionIndicator(state = state, onClick = onClick)
        VerticalDivider()
        // NavigationView()
    }
}

@Composable
fun PolluitionIndicator(state: MeterState, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        CircularSpeedIndicator(state.arcValue, 240f)
        StartButton(!state.inProgress, onClick)
        SpeedValue(value = state.polutionText.toString())
    }
}

@Composable
fun SpeedValue(value: String) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "QUALIDADE DO AR")
        Text(
            text = value,
            fontSize = 45.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(text = "BOA")
    }
}

@Composable
fun CircularSpeedIndicator(value: Float, angle: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {
        drawLines(value, angle)
        drawArcs(value, angle)
    }
}

@Composable
fun StartButton(isEnabled: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.padding(bottom = 24.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(24.dp),
        //border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.)

    ) {
        Text(text = "START", modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
    }
}

//drawing indication lines
fun DrawScope.drawLines(progress: Float, maxValue: Float, numberOfLines: Int = 40) {
    val oneRotation = maxValue / numberOfLines
    val startValue = if (progress == 0f) 0 else floor(progress * numberOfLines).toInt() + 1

    for (i in startValue..numberOfLines) {
        rotate(i * oneRotation + (180 - maxValue) / 2) {
            drawLine(
                LightColor,
                Offset(if (i % 5 == 0) 80f else 30f, size.height / 2),
                Offset(0f, size.height / 2),
                8f,
                StrokeCap.Round
            )
        }

    }
}

//arcs with nested functions
fun DrawScope.drawArcs(progress: Float, maxValue: Float) {
    val startAngle = 270 - maxValue / 2
    val sweepAngle = maxValue * progress

    val topLeft = Offset(50f, 50f)
    val size = Size(size.width - 100f, size.height - 100f)

    fun drawBlur() {
        for (i in 0..20) {
            drawArc(
                color = Green200.copy(alpha = i / 900f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = 80f + (20 - i) * 20, cap = StrokeCap.Round)
            )
        }
    }

    fun drawStroke() {
        drawArc(
            color = Green500,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = 86f, cap = StrokeCap.Round)
        )
    }

    //gradient color giving new view o arc
    fun drawGradient() {
        drawArc(
            brush = GreenGradient,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = 80f, cap = StrokeCap.Round)
        )
    }

    //init functions
    drawBlur()
    drawStroke()
    drawGradient()
}


/*@Composable
fun NavigationView() {
    val items = listOf(
        R.drawable.wifi,
        R.drawable.person,
        R.drawable.speed,
        R.drawable.settings,
    )
    val selectedItem = 2

    BottomNavigation(backgroundColor = DarkColor) {
        items.mapIndexed { index, item ->
            BottomNavigationItem(
                selected = index == selectedItem,
                onClick = { },
                unselectedContentColor = MaterialTheme.colors.onSurface,
                icon = {
                    Icon(painterResource(id = item), null)
                }
            )
        }
    }
}*/

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF414D66))
            .width(1.dp)
    )
}