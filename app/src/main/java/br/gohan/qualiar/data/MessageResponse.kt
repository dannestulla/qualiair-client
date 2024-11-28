package br.gohan.qualiar.data

import kotlinx.serialization.Serializable

//AirQualityLevel(nivel=N2, descricao=Moderada, indice=42)
@Serializable
data class AirQualityLevel(
    val nivel: String,
    val descricao: String,
    val indice: Int
)
