package com.example.linenassignment

import android.text.format.DateFormat
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.util.*

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

fun BigDecimal.formatAmount(): String = DecimalFormat("#,##0.00").format(this)
fun BigInteger.toBigDecimal(): BigDecimal = BigDecimal(this)
fun String.toFormattedDate(): String {
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    calendar.timeInMillis = this.toLong() * 1000L
    return DateFormat.format("dd MMMM yyyy HH:mm", calendar).toString()
}

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE).apply {
        setAction(R.string.all_string_title) {
            dismiss()
        }
    }.show()
}

fun subUnitToBase(subunitAmount: String): String {
    return BigDecimal(subunitAmount)
        .divide(10.toBigDecimal().pow(6))
        .formatAmount()
}