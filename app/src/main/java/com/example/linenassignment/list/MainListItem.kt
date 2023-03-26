package com.example.linenassignment.list


sealed interface MainListItem {
    data class Balance(
        val currencyName: String,
        val currencyCode: String,
        val value: String
    ) : MainListItem

    data class Transaction(val id: String) : MainListItem
}
