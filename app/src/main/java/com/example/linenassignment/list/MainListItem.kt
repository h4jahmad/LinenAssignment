package com.example.linenassignment.list

import java.math.BigDecimal

sealed interface MainListItem {
    data class Balance(val currency: String, val value: BigDecimal) : MainListItem
    data class Transaction(val id: String) : MainListItem
}
