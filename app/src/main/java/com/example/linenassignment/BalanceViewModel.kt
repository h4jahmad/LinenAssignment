package com.example.linenassignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.linenassignment.MainModule.getTokenContract
import com.example.linenassignment.list.MainListItem
import com.example.linenassignment.list.MainListItem.Balance
import com.example.linenassignment.model.Progress
import io.reactivex.Flowable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.bouncycastle.util.encoders.Base64
import org.web3j.abi.EventValues
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.Uint
import org.web3j.ens.contracts.generated.ENS.TransferEventResponse
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.utils.Convert

class BalanceViewModel(private val web3j: Web3j) : ViewModel() {

    /**
     * Instead of hardcoding, I'd have a `query` [kotlinx.coroutines.flow.StateFlow] and I'll update it from the UI. Then
     * I'll have a separate [kotlinx.coroutines.flow.StateFlow] for `balance` and I'll get the balance from that. I'd also
     * try to get the currency from a datasource(either remote or local) instead of having it
     * from a constant.
     * Additionally, I'd utilize the Android App Architecture and would have `UI States` that
     * store queries, results, etc.
     * */
    private val _mainList = MutableStateFlow<List<MainListItem>>(emptyList())
    val mainList = _mainList.asStateFlow()

    private val _shouldRefresh = MutableStateFlow(
        Progress(
            shouldRefresh = true,
            isFirstTimeLoading = true
        )
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            _shouldRefresh.collectLatest {
                if (it.shouldRefresh) refreshAll(it.isFirstTimeLoading)
            }
        }
    }

    private fun refreshAll(isFirstTimeLoading: Boolean) {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            if (isFirstTimeLoading) {
                _isLoading.update { true }
            } else {
                _isRefreshing.update { true }
            }

            val ethBalanceDeferred = async { fetchEthBalance(ADDRESS) }
            val tokenBalanceDeferred = async { fetchTokenBalance(ADDRESS, USDC_CONTRACT_ADDRESS) }
            val ethBalance = ethBalanceDeferred.await()
            val tokenBalance = tokenBalanceDeferred.await()

            _mainList.update {
                listOf<MainListItem>(
                    ethBalance,
                    tokenBalance
                )
            }
            if (isFirstTimeLoading) {
                _isLoading.update { false }
            } else {
                _isRefreshing.update { false }
                setShouldRefresh(
                    shouldRefresh = false,
                    isFirstTimeLoading = false
                )
            }
        }
    }

    fun setShouldRefresh(
        shouldRefresh: Boolean,
        isFirstTimeLoading: Boolean
    ) {
        _shouldRefresh.update {
            it.copy(
                shouldRefresh = shouldRefresh,
                isFirstTimeLoading = isFirstTimeLoading
            )
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
        val contract = getTokenContract(
            tokenAddress,
            web3j,
            MainModule.getReadOnlyTransactionManager(web3j, address),
        )
        val balance = contract.balanceOf(address).sendAsync()
        val formattedBalance = balance.await()
            .toBigDecimal()
            .formatAmount()

        Balance(
            currencyName = USDC_NAME,
            currencyCode = USDC_CODE,
            value = formattedBalance
        )
    }

    private suspend fun fetchLastIncomingTransactions(
        address: String,
        tokenAddress: String,
        count: Long = 50
    ) {
        val contract = getTokenContract(
            tokenAddress,
            web3j,
            MainModule.getReadOnlyTransactionManager(web3j, address),
        )
        val filter = EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            tokenAddress
        ).addOptionalTopics(
            null, Address(address).value
        )

        val event = Event(
            "Transfer",
            listOf(
                object: TypeReference<Address>() {},
                object: TypeReference<Address>() {},
                object: TypeReference<Uint>() {}
            )
        )

//        Flowable.fromPublisher(contract.transferEventFlowable(filter))
//            .take(count)
//            .blockingIterable()
//            .mapNotNull { event ->
//                val values = EventValues
//            }
    }

    override fun onCleared() {
        web3j.shutdown()
        refreshJob?.cancel()
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


