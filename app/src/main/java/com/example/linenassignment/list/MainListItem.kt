package com.example.linenassignment.list

import androidx.annotation.StringRes
import java.math.BigInteger

/**
 * Instead of directly using the currency's name and its code, I'd use [java.util.Currency]
 * class in order to maintain a better communication with other APIs.
 * */
sealed interface MainListItem {
    data class Balance(
        val currencyName: String,
        val currencyCode: String,
        val value: String,
    ) : MainListItem

    /** Instead of passing the resource Id directly and making the ViewModel
     * dependent on the android resources, I'd use sealed classes, to let the
     * ViewModel be testable inside the jvm test suit.
     */
    data class Separator(@StringRes val titleResId: Int) : MainListItem

    data class Transaction(
        val hash: String,
        val fromAddress: String,
        val amount: BigInteger,
        val currencyCode: String,
        val formattedDateTime: String,
    ) : MainListItem
}
