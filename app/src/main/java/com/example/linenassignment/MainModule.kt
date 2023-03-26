package com.example.linenassignment

import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

// It's better to use dependency Injection to build the Web3J object.
object MainModule {

    fun getWeb3Instance(): Web3j = Web3j.build(HttpService(BuildConfig.BASE_URL + ethEndpoint))

}