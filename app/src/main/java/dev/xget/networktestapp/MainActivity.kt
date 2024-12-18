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
                initialValue = NetworkStatus.Unknown,
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

@Composable
fun NetworkMainScreen(
    networkStatusFlow: StateFlow<NetworkStatus>,
    networkSpeedTestResultFlow: StateFlow<NetworkTestResult>,
    onStartSpeedTest: () -> Unit
) {
    val networkStatus = networkStatusFlow.collectAsState()
    val networkSpeedTestResult = networkSpeedTestResultFlow.collectAsState()

    Box(
        modifier = Modifier
            .padding(16.dp)
            .padding(top = 70.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {


            Spacer(modifier = Modifier.padding(top = 30.dp))

            when (networkStatus.value) {
                is NetworkStatus.Connected -> {


                    Spacer(modifier = Modifier.padding(top = 100.dp))

                    if (networkSpeedTestResult.value is NetworkTestResult.Testing && !(networkSpeedTestResult.value as NetworkTestResult.Testing).initialized) {
                        Image(
                            painter = painterResource(id = R.drawable.img_speed_test),
                            contentDescription = "SpeedTester Logo",
                            modifier = Modifier
                                .padding(top = 30.dp)
                                .fillMaxWidth()
                        )

                        Text(
                            text = "Connected Successfully",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }


                    if (networkSpeedTestResult.value is NetworkTestResult.Testing && (networkSpeedTestResult.value as NetworkTestResult.Testing).initialized) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Download Speed",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Arrow",
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .rotate(-90f),
                                    tint = Blue
                                )
                            }
                            Spacer(modifier = Modifier.padding(top = 16.dp))

                            if (networkSpeedTestResult.value is NetworkTestResult.Testing && (networkSpeedTestResult.value as NetworkTestResult.Testing).downloadInProgress) {
                                CircularProgressIndicator(
                                    color = BlueWhite
                                )
                            } else {
                                Text(
                                    text = "${(networkSpeedTestResult.value as NetworkTestResult.Testing).downloadSpeed} Mb/s",
                                    fontSize = 24.sp,
                                )
                            }

                        }

                        Spacer(modifier = Modifier.padding(top = 30.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Upload Speed",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Arrow",
                                    modifier = Modifier
                                        .padding(start = 24.dp)
                                        .rotate(90f),
                                    tint = Blue
                                )
                            }
                            Spacer(modifier = Modifier.padding(top = 16.dp))

                            if (networkSpeedTestResult.value is NetworkTestResult.Testing && (networkSpeedTestResult.value as NetworkTestResult.Testing).uploadInProgress) {
                                CircularProgressIndicator(
                                    color = Blue
                                )
                            } else {
                                Text(
                                    text = "${(networkSpeedTestResult.value as NetworkTestResult.Testing).uploadSpeed} Mb/s",
                                    fontSize = 24.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(top = 30.dp))

                        Divider()


                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "More Info",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Ping Icon",
                                    modifier = Modifier
                                        .padding(start = 8.dp),
                                    tint = Blue
                                )
                            }

                            if (networkSpeedTestResult.value is NetworkTestResult.Testing &&
                                (networkSpeedTestResult.value as NetworkTestResult.Testing).onPingInProgress
                            ) {
                                CircularProgressIndicator(
                                    color = BlueWhite,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            } else {
                                val ping =
                                    (networkSpeedTestResult.value as? NetworkTestResult.Testing)?.ping
                                        ?: 0
                                val jitter =
                                    (networkSpeedTestResult.value as? NetworkTestResult.Testing)?.jitter
                                        ?: 0
                                val serverUrl =
                                    (networkSpeedTestResult.value as? NetworkTestResult.Testing)?.serverUrl
                                        ?: ""
                                Text(
                                    text = "Ping: $ping ms",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 16.dp)
                                )

                                Text(
                                    text = "Jitter: $jitter ms",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                if (serverUrl.isNotEmpty()) {
                                    Text(
                                        text = "Server: $serverUrl",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }

                            }
                        }
                    }

                }

                is NetworkStatus.Disconnected -> {
                    Spacer(modifier = Modifier.padding(top = 40.dp))
                    Text(
                        text = "Sorry you are disconnected of a network, please connect to a network to test",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is NetworkStatus.Unknown -> {
                    Spacer(modifier = Modifier.padding(top = 40.dp))
                    Text(
                        text = "Sorry you are disconnected of a network or there is an error, please connect to a network to test or restart the app",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 50.dp))
        }


        Button(
            onClick = onStartSpeedTest,
            enabled = networkStatus.value is NetworkStatus.Connected && (networkSpeedTestResult.value is NetworkTestResult.Testing && !(networkSpeedTestResult.value as NetworkTestResult.Testing).initialized),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue
            )
        ) {
            Text("Start Speed Test")
        }
    }


}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PreviewNetworkMainScreen() {
    NetworkTestAppTheme {
    }

}