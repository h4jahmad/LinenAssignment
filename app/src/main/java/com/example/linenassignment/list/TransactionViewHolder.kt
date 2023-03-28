package com.example.linenassignment.list

import androidx.recyclerview.widget.RecyclerView
import com.example.linenassignment.databinding.ItemMainListBalanceBinding
import com.example.linenassignment.databinding.ItemMainListTransactionBinding
import com.example.linenassignment.list.MainListItem.Balance
import com.example.linenassignment.list.MainListItem.Transaction
import java.util.*

/**
 * Instead of inheriting directly from `RecyclerView.ViewHolder`, I'd create a `BaseViewHolder`
 * class and add a `bind(item: T)` method in it.
 * */
class TransactionViewHolder(private val binding: ItemMainListTransactionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Transaction): Unit = with(binding) {
        tt.text = item.id
    }
}