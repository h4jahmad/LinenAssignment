package com.example.linenassignment

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger

// I'd use a DI library(e.g. hilt or dagger) to build the dependencies.
object MainModule {

    private fun provideHttpClient(debuggable: Boolean) = OkHttpClient.Builder().apply {
        if (debuggable) {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
    }.build()

    fun provideWeb3(debuggable: Boolean = true): Web3j = Web3j.build(
        HttpService(
            BuildConfig.BASE_URL,
            provideHttpClient(debuggable)
        )
    )

    fun provideReadOnlyTransactionManager(web3j: Web3j, address: String): TransactionManager =
        ReadonlyTransactionManager(web3j, address)

    fun provideTokenContract(
        tokenAddress: String,
        web3j: Web3j,
        transactionManager: TransactionManager,
    ): ERC20 = ERC20.load(
        tokenAddress,
        web3j,
        transactionManager,
        StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO)
    )

}