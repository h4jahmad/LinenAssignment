package com.example.linenassignment

import android.text.format.DateFormat
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resumeWithException

inline fun <reified T> Fragment.collectWithLifecycle(
    flow: Flow<T>,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline collector: suspend (T) -> Unit,
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(lifecycleState) {
            flow.collectLatest { collector(it) }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> CompletableFuture<T>.await(): T = suspendCancellableCoroutine { cont ->
    whenComplete { result, exception ->
        exception?.let(cont::resumeWithException) ?: cont.resume(result) {
            cancel(true)
        }
    }
}

fun BigDecimal.formatAmount(): String = DecimalFormat("#,##0.00").format(this)
fun BigInteger.toFormattedAmount(): String = DecimalFormat("#,##0.#").format(this)
fun BigInteger.toBigDecimal(): BigDecimal = BigDecimal(this)
fun BigInteger.toFormattedDate(): String {
    val longTimeStamp = this.divide(1000.toBigInteger()).toLong()
    return DateFormat.format("yyyy-MM-dd HH:mm:ss", longTimeStamp).toString()
}

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE).apply {
        setAction(R.string.all_string_title) {
            dismiss()
        }
    }.show()
}