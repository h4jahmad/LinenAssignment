package com.example.linenassignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.linenassignment.MainModule.provideTokenContract
import com.example.linenassignment.list.MainListItem
import com.example.linenassignment.list.MainListItem.Balance
import com.example.linenassignment.list.MainListItem.Separator
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
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

    private val _uiState = MutableStateFlow(UiState(isFirstTimeLoading = true))
    val uiState = _uiState.asStateFlow()

    private val compositeDisposable = CompositeDisposable()

    private var refreshJob: Job? = null
    private var fetchJob: Job? = null

    private val jobExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.message?.let(::setErrorMessage)
        fetchJob?.start()
    }

    init {
        refreshJob = viewModelScope.launch {
            while (true) {
                setRefreshing()
//                delay(5000) TODO: Handle refresh
            }
        }

        fetchJob = viewModelScope.launch(context = jobExceptionHandler) {
            _uiState.collectLatest { state ->
                if (state.isRefreshing) {
                    fetchAll(this@launch)
                    setLoaded()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
        compositeDisposable.dispose()
        web3j.shutdown()
    }

    private suspend fun fetchAll(coroutineScope: CoroutineScope) = with(coroutineScope) {
        val ethBalanceDeferred = async { fetchEthBalance(ADDRESS) }
        val tokenBalanceDeferred = async { fetchTokenBalance(ADDRESS, USDC_CONTRACT_ADDRESS) }
        val ethBalance = ethBalanceDeferred.await()
        val tokenBalance = tokenBalanceDeferred.await()
        val incomingTransactions = fetchLastIncomingTransactions(ADDRESS, USDC_CONTRACT_ADDRESS)

        _mainList.update {
            mutableListOf<MainListItem>(
                Separator(R.string.list_wallet_balance),
                ethBalance,
                tokenBalance,
                Separator(R.string.list_token_transactions),
            ).apply {
                addAll(
                    listOf(
                        MainListItem.Transaction("23532"),
                        MainListItem.Transaction("235532"),
                        MainListItem.Transaction("325523"),
                        MainListItem.Transaction("532")
                    )
                )
            }
        }
    }

    fun shouldRefresh() {
        setRefreshing()
    }

    private fun setRefreshing() {
        _uiState.update {
            it.copy(
                isFirstTimeLoading = false,
                isRefreshing = true,
                isLoaded = false,
                errorMessage = null
            )
        }
    }

    private fun setLoaded() {
        _uiState.update {
            it.copy(
                isFirstTimeLoading = false,
                isRefreshing = false,
                isLoaded = true,
                errorMessage = null
            )
        }
    }

    private fun setErrorMessage(errorMessage: String) {
        _uiState.update {
            it.copy(
                isFirstTimeLoading = false,
                isRefreshing = false,
                isLoaded = false,
                errorMessage = errorMessage
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
        val contract = provideTokenContract(
            tokenAddress,
            web3j,
            MainModule.provideReadOnlyTransactionManager(web3j, address),
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
        walletAddress: String,
        tokenAddress: String,
        count: Long = 50
    ) {

        //Get Block Counts
        val blockCount = getWalletBlockCount(walletAddress, tokenAddress)
//        val startBlockName: String =
//            if (blockCount > count) {
//                val startBlockIndex = blockCount - count
//                val startBlock = getBlockName()
//                ""
//            } else {
//                DefaultBlockParameterName.EARLIEST.value
//            }

        val transferEvent = Event("Transfer",
            listOf<TypeReference<*>>(
                object : TypeReference<Address>(true) {},
                object : TypeReference<Address>(true) {},
                object : TypeReference<Uint256>(false) {}
            )
        )
        val filter = EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            tokenAddress
        ).addOptionalTopics(
            null,
            walletAddress,
            tokenAddress
        )

//        val subscription = web3j.ethLogFlowable(filter)
//            .subscribe

//        val disposable = web3j
//            .ethLogFlowable(filter)
//            .subscribe(
//                {
//                    println(it.transactionHash)
//                },
//                {
//                    it.message?.let(::setErrorMessage)
//                }
//            )
//        val receipts = web3j.ethGetLogs(filter)
//            .sendAsync()
//            .await()
//            .logs
//            .take(50)
//            .map { log ->
//                println(log)
//            }
    }

    private suspend fun getWalletBlockCount(walletAddress: String, tokenAddress: String): Int {
        val latestBlockNumber = web3j.ethBlockNumber()
        return 0
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val web3j = MainModule.provideWeb3()
                BalanceViewModel(web3j)
            }
        }
    }
}

data class UiState(
    val isFirstTimeLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null
)

