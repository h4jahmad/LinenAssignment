package com.example.linenassignment.list

import androidx.recyclerview.widget.RecyclerView
import com.example.linenassignment.databinding.ItemMainListTransactionBinding
import com.example.linenassignment.list.MainListItem.Transaction
import com.example.linenassignment.toFormattedAmount

/**
 * Instead of inheriting directly from `RecyclerView.ViewHolder`, I'd create a `BaseViewHolder`
 * class and add a `bind(item: T)` method in it.
 * */
class TransactionViewHolder(private val binding: ItemMainListTransactionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Transaction): Unit = with(binding) {
        itemMainListTransactionDateTimeValue.text = item.formattedDateTime
        itemMainListTransactionAmount.text = item.amount.toFormattedAmount()
        itemMainListTransactionCurrency.text = item.currencyCode
        itemMainListTransactionFromAddress.text = item.fromAddress
        itemMainListTransactionHash.text = item.hash
    }
}