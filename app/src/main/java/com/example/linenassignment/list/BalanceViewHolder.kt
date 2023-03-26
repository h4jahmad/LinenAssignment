package com.example.linenassignment.list

import androidx.recyclerview.widget.RecyclerView
import com.example.linenassignment.R
import com.example.linenassignment.databinding.ItemMainListBalanceBinding
import com.example.linenassignment.list.MainListItem.Balance

/**
 * Instead of inheriting directly from `RecyclerView.ViewHolder`, I'd create a `BaseViewHolder`
 * class and add a `bind(item: T)` method to it.
 * */
class BalanceViewHolder(private val binding: ItemMainListBalanceBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Balance): Unit = with(binding) {
        itemMainListBalanceTitle.text = String.format(
            root.context.getString(R.string.all_asset_balance_label),
            item.currencyName
        )
        itemMainListBalanceCurrencyLabel.text = item.currencyCode
        itemMainListBalanceAmountLabel.text = item.value
    }
}