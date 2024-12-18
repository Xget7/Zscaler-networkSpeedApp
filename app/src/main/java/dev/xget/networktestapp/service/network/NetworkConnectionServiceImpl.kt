package dev.xget.networktestapp.service.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.speedchecker.android.sdk.Public.SpeedTestListener
import com.speedchecker.android.sdk.Public.SpeedTestResult
import com.speedchecker.android.sdk.SpeedcheckerSDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

//Created service to scalability and use in other places
class NetworkConnectionServiceImpl(
    private val context: Context,
) : NetworkConnectionService {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    override val networkStatus: Flow<NetworkStatus> = callbackFlow {
        val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)


                val nc: NetworkCapabilities? =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

                trySend(
                    NetworkStatus.Connected
                )
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(NetworkStatus.Disconnected)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(request, connectivityCallback)

        //Finish flow when coroutine is cancelled
        awaitClose {
            connectivityManager.unregisterNetworkCallback(connectivityCallback)
        }

    }
        .distinctUntilChanged() //Only send new values
        .flowOn(Dispatchers.IO) //Run on IO thread


    override val networkSepedTestResult: Flow<NetworkTestResult> = callbackFlow {
        var currentState  = NetworkTestResult.Testing()

        val speedTestCallback = object : SpeedTestListener {
            override fun onTestStarted() {
                currentState = NetworkTestResult.Testing(initialized = false)
                trySend(currentState)
            }

            override fun onFetchServerFailed(p0: Int?) {
                trySend(NetworkTestResult.Failure("Failed to fetch server"))
                Log.d("SpeedTest", "onFetchServerFailed")
            }

            override fun onFindingBestServerStarted() {
                // Update state for server finding
                currentState = currentState.copy(initialized = true)
                trySend(currentState)
            }

            override fun onTestFinished(p0: SpeedTestResult?) {
                // Update state for test completion
                currentState = currentState.copy(
                    uploadInProgress = false,
                    downloadInProgress = false,
                    onPingInProgress = false,
                    serverUrl = p0?.server?.Domain ?: "",
                    finished = true
                )
                trySend(currentState)
            }

            override fun onPingStarted() {
                currentState = currentState.copy(onPingInProgress = true)
                trySend(currentState)
            }

            override fun onPingFinished(p0: Int, p1: Int) {
                currentState = currentState.copy(
                    onPingInProgress = false,
                    ping = p0,
                    jitter = p1
                )
                trySend(currentState)
            }

            override fun onDownloadTestStarted() {
                currentState = currentState.copy(downloadInProgress = true)
                trySend(currentState)
            }

            override fun onDownloadTestProgress(p0: Int, p1: Double, p2: Double) {
                currentState = currentState.copy(downloadSpeed = p0)
                trySend(currentState)
            }

            override fun onDownloadTestFinished(p0: Double) {
                currentState = currentState.copy(downloadInProgress = false, downloadSpeed = p0.toInt())
                trySend(currentState)
            }

            override fun onUploadTestStarted() {
                currentState = currentState.copy(uploadInProgress = true)
                trySend(currentState)
            }

            override fun onUploadTestProgress(p0: Int, p1: Double, p2: Double) {
                currentState = currentState.copy(uploadSpeed = p0)
                trySend(currentState)
            }

            override fun onUploadTestFinished(p0: Double) {
                currentState = currentState.copy(uploadInProgress = false, uploadSpeed = p0.toInt())
                trySend(currentState)
            }

            override fun onTestWarning(p0: String?) {
                trySend(NetworkTestResult.Failure("Warning: $p0"))
            }

            override fun onTestFatalError(p0: String?) {
                trySend(NetworkTestResult.Failure("Fatal error: $p0"))
            }

            override fun onTestInterrupted(p0: String?) {
                trySend(NetworkTestResult.Failure("Test interrupted: $p0"))
            }
        }

        SpeedcheckerSDK.SpeedTest.setOnSpeedTestListener(speedTestCallback)

        // Close the flow when the coroutine is canceled
        awaitClose {
            close()
        }
    }

    fun startSpeedTest() {
        SpeedcheckerSDK.SpeedTest.startTest(context)
    }

}
