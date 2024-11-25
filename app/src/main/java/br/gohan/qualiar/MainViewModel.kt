package br.gohan.qualiar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.qualiar.data.MainRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {
    private lateinit var token : String

    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    val model =
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.API_KEY
        )

    fun sendPrompt(
        airQuality: String
    ) {
        _uiState.value = UiState.Loading
        val promptIA = "me de recomendacoes para a seguinte poluicao do ar, em portugues e em apenas 3 bullet points, sendo que a localizacao e sao paulo e a pessoa tem 60 anos de idade + $airQuality"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = model.generateContent(
                    content {
                        text(promptIA)
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = UiState.SuccessAI(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(Exception(e.localizedMessage ?: ""))
            }
        }
    }

    fun startNotificationService() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    token = task.result
                    viewModelScope.launch {
                        mainRepository.apply {
                            saveToken(token)
                            //generateIndex()
                            val response = getAirPollutionData(token)
                            _uiState.emit(response)
                        }
                    }
                }
            }
    }

    fun getAirPollution() {
        viewModelScope.launch {
            mainRepository.getAirPollutionData(token)
        }
    }
}