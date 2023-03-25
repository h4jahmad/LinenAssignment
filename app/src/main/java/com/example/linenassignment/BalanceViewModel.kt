package com.example.linenassignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linenassignment.list.MainListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.util.*

class BalanceViewModel : ViewModel() {

    /**
     * Instead of hardcoding, I'd have a `query` [StateFlow] and I'll update it from the UI. Then
     * I'll have a separate [StateFlow] for `balance` and I'll get the balance from that. I'd also
     * try to get the currency from a datasource(either remote or local) instead of having it
     * from a constant.
     * */
    private val _ethBalance = MutableStateFlow(
        MainListItem.Balance(
            currency = ETH_CODE,
            value = "0.00"
        )
    )
    val ethBalance = _ethBalance.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        getEthBalance()
    }

    /**
     * Instead of directly handling the business layer in a `ViewModel`, I'd facilitate the
     * Clean Architecture and used and combination of _Repository Layer + Clean Architecture_.
     * */
    fun getEthBalance() {
        viewModelScope.launch {
            _isLoading.update { true }
            withContext(Dispatchers.IO) {
                val web3 = Web3j.build(HttpService(BuildConfig.BASE_URL))
                val address = ADDRESS
                val balanceResponse =
                    web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync()
                val r = balanceResponse.await().toString()
                val value = Convert.fromWei(
                    r,
                    Convert.Unit.ETHER
                )
                _ethBalance.update {

                    MainListItem.Balance(
                        currency = ETH_CODE,
                        value = value.formatEth()
                    )
                }
                _isLoading.update { false }
            }
        }
    }
}


