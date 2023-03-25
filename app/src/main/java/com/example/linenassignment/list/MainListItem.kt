package com.example.linenassignment.list


sealed interface MainListItem {
    data class Balance(
        val currency: String,
        val value: String
    ) : MainListItem
    data class Transaction(val id: String) : MainListItem
}
