package br.gohan.qualiar

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.qualiar.data.MainRepository
import br.gohan.qualiar.data.NetworkState
import br.gohan.qualiar.helpers.Location
import br.gohan.qualiar.ui.UiState
import br.gohan.qualiar.ui.toUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val location: Location,
    private val mainRepository: MainRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        observeRepository()
        startService()
    }

    private fun observeRepository() {
        viewModelScope.launch {
            mainRepository.networkState.map { networkState ->
                when (networkState) {
                    is NetworkState.SuccessBackend ->
                        UiState.Success(
                            networkState.toUiState(),
                            location = location
                        )

                    is NetworkState.Error -> UiState.Error(
                        message = networkState.errorMessage
                    ) {
                        startService()
                    }

                    is NetworkState.SuccessAI -> networkState.toUiState(uiState.value, location)
                    is NetworkState.Initial -> UiState.Loading()
                }
            }.collect { transformedState ->
                _uiState.value = transformedState
            }
        }
    }

    private fun setInitialState(): UiState {
        val loading = UiState.Loading(location.city)
        _uiState.value = loading
        return loading
    }

    @SuppressLint("MissingPermission")
    private fun startService() {
        setInitialState()
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.apply {
                saveToken()
                if (location.shouldUpdate) {
                    generateIndex(
                        location
                    )
                }
                getAirQualityLevel()?.let {
                    sendIaPrompt(promptAI + location + it)
                }
            }
        }
    }

    fun startedMeter() {
        _uiState.update { state ->
            if (state is UiState.Success) {
                state.copy(
                    meterState = state.meterState.copy(
                        started = true,
                        enableButton = false
                    ),
                )
            } else {
                state
            }
        }
    }
}
