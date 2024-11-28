package br.gohan.qualiar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.qualiar.data.MainRepository
import br.gohan.qualiar.data.NetworkState
import br.gohan.qualiar.ui.UiState
import br.gohan.qualiar.ui.toUiState
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        startService()
        observeRepository()
    }

    private fun observeRepository() {
        viewModelScope.launch {
            mainRepository.networkState.map { networkState ->
                when (networkState) {
                    is NetworkState.Initial -> UiState.Loading
                    is NetworkState.SuccessBackend ->
                        UiState.Success(networkState.toUiState())

                    is NetworkState.Error -> UiState.Error(
                        message = networkState.errorMessage.message ?: "Um erro ocorreu"
                    )

                    is NetworkState.SuccessAI -> networkState.toUiState(uiState.value)
                }
            }.collect { transformedState ->
                _uiState.value = transformedState
            }
        }
    }

    private fun startService() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch(Dispatchers.IO) {
                        mainRepository.apply {
                            saveToken(task.result)
                            getAirQualityLevel()?.let {
                                sendIaPrompt(BuildConfig.PROMPT_IA + it)
                            }
                        }
                    }
                }
            }
    }
}