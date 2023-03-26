package com.example.linenassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linenassignment.databinding.FragmentBalanceBinding
import com.example.linenassignment.list.MainListAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BalanceFragment : Fragment() {

    private var _binding: FragmentBalanceBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("_binding is not initialized.")

    private val viewModel by viewModels<BalanceViewModel> { BalanceViewModel.FACTORY }

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
        with(mainList) {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mainAdapter
        }

        collectWithLifecycle(viewModel.isLoading, collector = ::handleProgress)

        collectWithLifecycle(viewModel.mainList) { list ->
            mainAdapter.submitList(list)
        }
    }

    private fun handleProgress(isLoading: Boolean) = with(binding) {
        mainList.isVisible = !isLoading
        mainProgress.isVisible = isLoading
    }

    private inline fun <reified T> collectWithLifecycle(
        flow: Flow<T>,
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        crossinline collector: suspend (T) -> Unit,
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(lifecycleState) {
                flow.collectLatest { collector(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}