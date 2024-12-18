package dev.xget.networktestapp

import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.speedchecker.android.sdk.SpeedcheckerSDK
import dev.xget.networktestapp.presentation.screens.NetworkMainScreen
import dev.xget.networktestapp.presentation.theme.Blue
import dev.xget.networktestapp.presentation.theme.BlueWhite
import dev.xget.networktestapp.service.network.NetworkConnectionServiceImpl
import dev.xget.networktestapp.service.network.NetworkStatus
import dev.xget.networktestapp.service.network.NetworkTestResult
import dev.xget.networktestapp.ui.theme.NetworkTestAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SpeedcheckerSDK.init(this)
        SpeedcheckerSDK.askPermissions(this);
        enableEdgeToEdge()
        val networkConnectivityService = NetworkConnectionServiceImpl(this)

        val networkStatus: StateFlow<NetworkStatus> =
            networkConnectivityService.networkStatus.stateIn(
                initialValue = NetworkStatus.Connected,
                scope = lifecycleScope,
                started = WhileSubscribed(5000)
            )
        val networkSpeedTestResult: StateFlow<NetworkTestResult> =
            networkConnectivityService.networkSepedTestResult.stateIn(
                initialValue = NetworkTestResult.Testing(),
                scope = lifecycleScope,
                started = WhileSubscribed(5000)
            )

        setContent {
            NetworkTestAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .padding(top = 30.dp)
                    ) {
                        Text(
                            text = "SpeedTest \uD83D\uDE80",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("This is the best network speed tester app ever")
                        Divider(
                            color = Color.LightGray,
                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                        )
                    }
                }) { innerPadding ->
                    innerPadding

                    NetworkMainScreen(networkStatus, networkSpeedTestResult) {
                        networkConnectivityService.startSpeedTest()
                    }
                }
            }
        }

    }
}