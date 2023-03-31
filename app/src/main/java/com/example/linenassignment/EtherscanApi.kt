package com.example.linenassignment

import com.example.linenassignment.model.EtherscanResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EtherscanApi {
    @GET("/api?module=account&action=tokentx")
    suspend fun fetchTransaction(
        @Query("address") address: String,
        @Query("contractaddress") contractAddress: String,
        @Query("page") page: Int,
        @Query("offset") offset: Int,
        @Query("sort") sort: String,
        @Query("apikey") apiKey: String
    ): EtherscanResponse

}