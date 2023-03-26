package com.example.linenassignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.linenassignment.list.MainListItem
import com.example.linenassignment.list.MainListItem.Balance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger

class BalanceViewModel(private val web3j: Web3j) : ViewModel() {

    /**
     * Instead of hardcoding, I'd have a `query` [kotlinx.coroutines.flow.StateFlow] and I'll update it from the UI. Then
     * I'll have a separate [kotlinx.coroutines.flow.StateFlow] for `balance` and I'll get the balance from that. I'd also
     * try to get the currency from a datasource(either remote or local) instead of having it
     * from a constant.
     * */
    private val _mainList = MutableStateFlow<List<MainListItem>>(emptyList())
    val mainList = _mainList.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoading.update { true }
            val mainList = async {
                val ethBalance = fetchEthBalance(ADDRESS)
                val tokenBalance = fetchTokenBalance(ADDRESS, USDC_CONTRACT_ADDRESS)
                listOf<MainListItem>(ethBalance, tokenBalance)
            }
            _mainList.update { mainList.await() }
            _isLoading.update { false }
        }
    }

//----------------------- Business Logic ---------------------------------------------
    /**
     * Instead of directly handling the business layer in a `ViewModel`, I'd facilitate the
     * Clean Architecture and used and combination of _Repository Layer + Clean Architecture_.
     * */
    private suspend fun fetchEthBalance(address: String): Balance = withContext(Dispatchers.IO) {
        val balanceResponse =
            web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync()
        val balance = balanceResponse
            .await()
            .balance
            .toBigDecimal()
        val formattedBalance = Convert.fromWei(
            balance,
            Convert.Unit.ETHER
        ).formatAmount()

        Balance(
            currencyName = ETH_NAME,
            currencyCode = ETH_CODE,
            value = formattedBalance
        )
    }

    private suspend fun fetchTokenBalance(
        address: String,
        tokenAddress: String
    ): Balance = withContext(Dispatchers.IO) {
        val token = ERC20.load(
            tokenAddress,
            web3j,
            Credentials.create(ECKeyPair(BigInteger.ZERO, BigInteger.ZERO)),
            StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO)
        )
        val balance = token.balanceOf(address).sendAsync()
        val formattedBalance = balance.await()
            .toBigDecimal()
            .formatAmount()

        Balance(
            currencyName = USDC_NAME,
            currencyCode = USDC_CODE,
            value = formattedBalance
        )
    }

    override fun onCleared() {
        web3j.shutdown()
        super.onCleared()
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val web3j = MainModule.getWeb3Instance()
                BalanceViewModel(web3j)
            }
        }
    }
}


