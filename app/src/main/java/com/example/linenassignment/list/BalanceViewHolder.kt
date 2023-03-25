package com.example.linenassignment.list

import androidx.recyclerview.widget.RecyclerView
import com.example.linenassignment.databinding.ItemMainListBalanceBinding
import com.example.linenassignment.list.MainListItem.Balance
import java.util.*

/**
 * Instead of inheriting directly from `RecyclerView.ViewHolder`, I'd create a `BaseViewHolder`
 * class and add a `bind(item: T)` method to it.
 * */
class BalanceViewHolder(private val binding: ItemMainListBalanceBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Balance): Unit = with(binding) {
        itemMainListBalanceCurrencyLabel.text = item.currency
        itemMainListBalanceAmountLabel.text = item.value
    }
}