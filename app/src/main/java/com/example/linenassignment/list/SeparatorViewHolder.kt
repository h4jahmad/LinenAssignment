package com.example.linenassignment.list

import androidx.recyclerview.widget.RecyclerView
import com.example.linenassignment.databinding.ItemMainListSeparatorBinding

class SeparatorViewHolder(private val binding: ItemMainListSeparatorBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MainListItem.Separator) = with(binding) {
        root.setText(item.titleResId)
    }
}