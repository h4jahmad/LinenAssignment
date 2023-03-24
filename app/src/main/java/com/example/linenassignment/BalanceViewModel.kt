package com.example.linenassignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

@HiltViewModel
class BalanceViewModel @Inject constructor() : ViewModel() {

    private val _ethBalance = MutableStateFlow(BigDecimal(0).apply {
        setScale(2)
    })
    val ethBalance = _ethBalance.asStateFlow()

    suspend fun getEthBalance() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val web3 = Web3j.build(HttpService(BuildConfig.BASE_URL))
                val address = "0x7DBB4bdCfE614398D1a68ecc219F15280d0959E0"
                val balanceResponse =
                    web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync()
                _ethBalance.update {
                    Convert.fromWei(
                        balanceResponse.await().toString(),
                        Convert.Unit.ETHER
                    ).setScale(2)
                }
            }
        }
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> CompletableFuture<T>.await(): T = suspendCancellableCoroutine { cont ->
    whenComplete { result, exception ->
        exception?.let(cont::resumeWithException) ?: cont.resume(result) {
            cancel(true)
        }
    }
}