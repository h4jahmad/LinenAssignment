package com.example.linenassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.linenassignment.databinding.FragmentBalanceBinding

class BalanceFragment : Fragment() {

    private var _binding: FragmentBalanceBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("_binding is not initialized.")

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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}