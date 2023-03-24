package com.example.linenassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.linenassignment.databinding.FragmentBalanceBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService

/**
 * I'd create a BaseFragment class, and add binding initializations, utily functions and
 * other Fragment-Specific methods to that.
 * */
@AndroidEntryPoint
class BalanceFragment : Fragment() {

    private var _binding: FragmentBalanceBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("_binding is not initialized.")

    private val viewModel: BalanceViewModel by viewModels()

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
        collectWithLifecycle(viewModel.ethBalance) { balance ->

        }
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