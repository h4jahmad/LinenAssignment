package com.example.linenassignment.model

import java.math.BigInteger

data class TransferEvent(
    val from: String,
    val to: String,
    val amount: BigInteger
)
