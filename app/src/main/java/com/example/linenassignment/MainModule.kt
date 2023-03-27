package com.example.linenassignment

import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger

// It's better to use dependency Injection to build the Web3J object.
object MainModule {

    fun getWeb3Instance(): Web3j = Web3j.build(HttpService(BuildConfig.BASE_URL))

    fun getReadOnlyTransactionManager(web3j: Web3j, address: String): TransactionManager =
        ReadonlyTransactionManager(web3j, address)

    fun getTokenContract(
        tokenAddress: String,
        web3j: Web3j,
        transactionManager: TransactionManager,
    ) = ERC20.load(
        tokenAddress,
        web3j,
        transactionManager,
        StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO)
    )

}