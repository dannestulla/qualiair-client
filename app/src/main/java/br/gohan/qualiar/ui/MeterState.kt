package br.gohan.qualiar.ui

//states model 
data class MeterState(
    val polutionText: Int = 0,
    val maxMeterValue: Float = 0F,
    val arcValue: Float = 0f,
    val description: String = "",
    val enableButton: Boolean = false,
    val started: Boolean = false
)
