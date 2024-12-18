package dev.xget.networktestapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.xget.networktestapp.R
import dev.xget.networktestapp.presentation.theme.Blue
import dev.xget.networktestapp.presentation.theme.BlueWhite
import dev.xget.networktestapp.service.network.NetworkStatus
import dev.xget.networktestapp.service.network.NetworkTestResult
import kotlinx.coroutines.flow.StateFlow

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

            Spacer(modifier = Modifier.padding(top = 10.dp))

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
                            text = stringResource(id = R.string.connection_success),
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
                                    text = stringResource(id = R.string.download_speed),
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
                                    text = stringResource(id = R.string.upload_speed),
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
                                    text = stringResource(id = R.string.more_info),
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
                                    text = "${stringResource(id = R.string.ping)}: $ping ms",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 16.dp)
                                )

                                Text(
                                    text = "${stringResource(id = R.string.jitter)}: $jitter ms",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                if (serverUrl.isNotEmpty()) {
                                    Text(
                                        text = "${stringResource(id = R.string.server)}: $serverUrl",
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
                        text = stringResource(id = R.string.disconnection),
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
                        text = stringResource(id = R.string.disconnection_unknown),
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
            Text(stringResource(id = R.string.start_speed_test))
        }
    }


}
