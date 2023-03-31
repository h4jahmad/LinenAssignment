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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Convert

class BalanceViewModel(
    private val web3j: Web3j,
    private val etherscan: EtherscanApi,
) : ViewModel() {

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

    private var refreshJob: Job? = null
    private var fetchJob: Job? = null

    private val jobExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.message?.let(::setErrorMessage)
        throwable.printStackTrace()
        fetchJob?.start()
    }

    init {
        refreshJob = viewModelScope.launch {
            while (true) {
                setRefreshing()
                delay(5000)
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
        fetchJob?.cancel()
        web3j.shutdown()
    }

    private suspend fun fetchAll(coroutineScope: CoroutineScope) = with(coroutineScope) {
        val ethBalanceDeferred = async { fetchEthBalance(ADDRESS) }
        val tokenBalanceDeferred = async { fetchTokenBalance(ADDRESS, USDC_CONTRACT_ADDRESS) }
        val ethBalance = ethBalanceDeferred.await()
        val tokenBalance = tokenBalanceDeferred.await()
        val incomingTransactions = fetchLastIncomingTransactions(
            walletAddress = ADDRESS,
            tokenAddress = USDC_CONTRACT_ADDRESS,
            tokenCode = USDC_CODE
        )

        _mainList.update {
            mutableListOf(
                Separator(R.string.list_wallet_balance),
                ethBalance,
                tokenBalance,
                Separator(R.string.list_token_transactions),
            ).apply {
                addAll(incomingTransactions)
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
     * Additionally, dispatchers should be injected instead of being hardcoded.
     * */
    private suspend fun fetchEthBalance(address: String): Balance =
        withContext(Dispatchers.IO) {
            val balanceResponse = web3j
                .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
            val balance = balanceResponse
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
        val balance = contract
            .balanceOf(address)
            .send()
        val formattedBalance = balance
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
        tokenCode: String,
        count: Int = 50
    ): List<MainListItem.Transaction> = withContext(Dispatchers.IO) {
        val transactions = etherscan.fetchTransaction(
            walletAddress,
            tokenAddress,
            1,
            count,
            "DESC",
            MainModule.API_KEY
        ).result
            .map { txn ->
                MainListItem.Transaction(
                    txn.hash,
                    txn.from,
                    subUnitToBase(txn.value),
                    tokenCode,
                    txn.timeStamp.toFormattedDate()
                )
            }

        transactions
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                BalanceViewModel(
                    MainModule.provideWeb3(),
                    MainModule.provideEtherscanApi()
                )
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

