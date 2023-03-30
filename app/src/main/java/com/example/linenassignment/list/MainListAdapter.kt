package com.example.linenassignment.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.linenassignment.R
import com.example.linenassignment.databinding.ItemMainListBalanceBinding
import com.example.linenassignment.databinding.ItemMainListSeparatorBinding
import com.example.linenassignment.databinding.ItemMainListTransactionBinding
import com.example.linenassignment.list.MainListItem.*

/**
 * It's better to create a BaseListAdapter to have some common functionalities so to avoid any
 * unnecessary repetitions.
 * */
class MainListAdapter : ListAdapter<MainListItem, RecyclerView.ViewHolder>(
    AsyncDifferConfig.Builder(COMPARATOR).build()
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_main_list_balance -> BalanceViewHolder(
            ItemMainListBalanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        R.layout.item_main_list_transaction -> TransactionViewHolder(
            ItemMainListTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        R.layout.item_main_list_separator -> SeparatorViewHolder(
            ItemMainListSeparatorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        else -> throw IllegalArgumentException("Invalid view type.")
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ): Unit = when (val item = getItem(position)) {
        is Balance -> (holder as BalanceViewHolder).bind(item)
        is Transaction -> (holder as TransactionViewHolder).bind(item)
        is Separator -> (holder as SeparatorViewHolder).bind(item)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Balance -> R.layout.item_main_list_balance
        is Transaction -> R.layout.item_main_list_transaction
        is Separator -> R.layout.item_main_list_separator
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<MainListItem>() {
            override fun areItemsTheSame(
                oldItem: MainListItem,
                newItem: MainListItem,
            ): Boolean = when {
                oldItem is Balance && newItem is Balance -> {
                    oldItem.currencyCode == newItem.currencyCode
                }
                oldItem is Transaction && newItem is Transaction -> {
                    oldItem.hash == newItem.hash
                }
                else -> false
            }

            override fun areContentsTheSame(
                oldItem: MainListItem,
                newItem: MainListItem
            ): Boolean = when {
                oldItem is Balance && newItem is Balance -> {
                    oldItem == newItem
                }
                oldItem is Transaction && newItem is Transaction -> {
                    oldItem == newItem
                }
                else -> false
            }
        }
    }

}
