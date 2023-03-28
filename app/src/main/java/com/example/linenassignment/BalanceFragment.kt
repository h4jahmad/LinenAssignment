package com.example.linenassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linenassignment.databinding.FragmentBalanceBinding
import com.example.linenassignment.list.MainListAdapter

class BalanceFragment : Fragment() {

    private var _binding: FragmentBalanceBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("_binding is not initialized.")

    private val viewModel: BalanceViewModel by viewModels { BalanceViewModel.FACTORY }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBalanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.initView()
    }

    private fun FragmentBalanceBinding.initView() {
        val mainAdapter = MainListAdapter()
        with(balanceList) {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mainAdapter
        }

        balanceSwipeRefresh.setOnRefreshListener(viewModel::shouldRefresh)

        collectWithLifecycle(viewModel.uiState) { state ->
            when {
                state.isFirstTimeLoading -> showProgress()
                state.isLoaded -> showList()
                state.isRefreshing -> balanceSwipeRefresh.isRefreshing = false
                state.errorMessage != null -> {
                    root.showSnackbar(state.errorMessage)
                    showList()
                }
            }
        }
        collectWithLifecycle(viewModel.mainList, collector = mainAdapter::submitList)
    }

    private fun showProgress() = with(binding) {
        balanceList.isVisible = false
        balanceProgress.isVisible = true
    }

    private fun showList() = with(binding) {
        balanceList.isVisible = true
        balanceProgress.isVisible = false
        balanceSwipeRefresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}