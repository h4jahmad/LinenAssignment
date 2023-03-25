package com.example.linenassignment

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.math.BigDecimal
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

fun BigDecimal.formatEth(): String = DecimalFormat("#,###.00").format(this)

