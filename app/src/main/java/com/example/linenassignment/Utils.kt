package com.example.linenassignment

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> CompletableFuture<T>.await(): T = suspendCancellableCoroutine { cont ->
    whenComplete { result, exception ->
        exception?.let(cont::resumeWithException) ?: cont.resume(result) {
            cancel(true)
        }
    }
}

fun BigDecimal.formatAmount(): String = DecimalFormat("#,##0.00").format(this)
fun BigInteger.toBigDecimal(): BigDecimal = BigDecimal(this)