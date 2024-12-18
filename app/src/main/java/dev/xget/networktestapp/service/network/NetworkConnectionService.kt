package dev.xget.networktestapp.service.network

import kotlinx.coroutines.flow.Flow

interface NetworkConnectionService {
    val networkStatus: Flow<NetworkStatus>
    val networkSepedTestResult: Flow<NetworkTestResult>
}

sealed class NetworkStatus {
    data object Connected : NetworkStatus()

    data object Disconnected : NetworkStatus()
    data object Unknown : NetworkStatus()

}

sealed class NetworkTestResult {
    data class Testing(
        val serverUrl: String = "",
        val initialized: Boolean = false,
        val uploadInProgress: Boolean = false,
        val downloadInProgress: Boolean = false,
        val uploadSpeed: Int = 0,
        val downloadSpeed: Int = 0,
        val onPingInProgress: Boolean = false,
        val ping: Int = 0,
        val jitter: Int = 0,
        val finished : Boolean = false
    ) : NetworkTestResult()

    data class Failure(val error: String) : NetworkTestResult()
}