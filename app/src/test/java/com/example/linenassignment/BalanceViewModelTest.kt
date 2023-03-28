package com.example.linenassignment

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.web3j.protocol.Web3j

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class BalanceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: BalanceViewModel
    @Before
    fun setUp() {
        viewModel = BalanceViewModel()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `wrong address, should result in false`() = runTest {

    }


}