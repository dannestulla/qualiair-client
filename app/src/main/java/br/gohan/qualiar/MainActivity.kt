package br.gohan.qualiar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import br.gohan.qualiar.helpers.Location
import br.gohan.qualiar.helpers.LocationHelper
import br.gohan.qualiar.ui.MainScreen
import br.gohan.qualiar.ui.new.AppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity(), KoinComponent {

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private val token = MutableStateFlow<String?>(null)
    private val location = MutableStateFlow<Location?>(null)

    private val locationAndToken = combine(
        location,
        token
    ) { location, token ->
        if (location != null && token != null) {
            val viewModel: MainViewModel = get { parametersOf(location, token) }
            startApp(viewModel)
        } else {
            loadingScreen()
        }
    }.launchIn(
        lifecycleScope
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.values.all { it }) {
                checkForTokenAndLocation()
            } else {
                showErrorMessage()
            }
        }
        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            checkForTokenAndLocation()
        }
    }

    private fun showErrorMessage() {
        Toast.makeText(
            this,
            "Todas as permissões são necessárias para o aplicativo funcionar.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun checkForTokenAndLocation() {
        lifecycleScope.launch {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { newToken ->
                token.update { newToken }
            }
            LocationHelper(this@MainActivity).getLastKnownLocation { newLocation ->
                location.update { newLocation }
            }
        }
    }

    private fun startApp(viewModel: MainViewModel) {
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }

    private fun loadingScreen() {
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
