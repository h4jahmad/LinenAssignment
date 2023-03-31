package com.example.linenassignment.list

import androidx.recyclerview.widget.RecyclerView
import com.example.linenassignment.databinding.ItemMainListTransactionBinding
import com.example.linenassignment.list.MainListItem.Transaction

/**
 * Instead of inheriting directly from `RecyclerView.ViewHolder`, I'd create a `BaseViewHolder`
 * class and add a `bind(item: T)` method in it.
 * */
class TransactionViewHolder(
    private val binding: ItemMainListTransactionBinding,
    private val onItemClicked: OnItemClicked
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Transaction): Unit = with(binding) {
        root.setOnClickListener {
            onItemClicked(item.hash)
        }
        itemMainListTransactionDateTimeValue.text = item.formattedDateTime
        itemMainListTransactionAmount.text = item.amount
        itemMainListTransactionCurrency.text = item.currencyCode
        itemMainListTransactionFromAddress.text = item.fromAddress
        itemMainListTransactionHash.text = item.hash
    }
}