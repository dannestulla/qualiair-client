package br.gohan.qualiar

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.qualiar.data.MainRepository
import br.gohan.qualiar.data.NetworkState
import br.gohan.qualiar.helpers.LocationHelper
import br.gohan.qualiar.ui.UiState
import br.gohan.qualiar.ui.toUiState
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        getToken()
        observeRepository()
    }

    private fun observeRepository() {
        viewModelScope.launch {
            mainRepository.networkState.map { networkState ->
                when (networkState) {
                    is NetworkState.SuccessBackend ->
                        UiState.Success(
                            networkState.toUiState(),
                            location = LocationHelper.currentLocation.value
                        )

                    is NetworkState.Error -> UiState.Error(
                        message = networkState.errorMessage
                    ) {
                        startService(mainRepository.token)
                    }

                    is NetworkState.SuccessAI -> networkState.toUiState(uiState.value)
                    is NetworkState.Initial -> UiState.Loading()
                }
            }.collect { transformedState ->
                _uiState.value = transformedState
            }
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startService(task.result)
                }
            }
    }

    private fun setInitialState(): UiState {
        val loading = UiState.Loading(LocationHelper.currentLocation.value?.city)
        _uiState.value = loading
        return loading
    }

    @SuppressLint("MissingPermission")
    private fun startService(token: String) {
        setInitialState()
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.apply {
                saveToken(token)
                LocationHelper.currentLocation.collect { location ->
                    if (location == null) {
                        return@collect
                    }
                    if (location.shouldUpdate) {
                        generateIndex(
                            location
                        )
                    }
                    getAirQualityLevel()?.let {
                        sendIaPrompt(promptAI + LocationHelper.currentLocation + it)
                    }
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
