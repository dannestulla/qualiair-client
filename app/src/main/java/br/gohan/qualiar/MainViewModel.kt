package br.gohan.qualiar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository : MainRepository
) : ViewModel() {

    fun startNotificationService() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.let { token ->
                        Log.d("FCM", "Token for this install: $token")
                        sendTokenToApi(token)
                    }
                }
            }
    }

    private fun sendTokenToApi(token: String) {
        viewModelScope.launch {
            mainRepository.sendToken(token)
        }
    }
}