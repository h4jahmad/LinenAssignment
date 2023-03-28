package com.example.linenassignment

import com.example.linenassignment.model.TransferEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.Log
import org.web3j.abi.EventEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.Uint
import org.web3j.protocol.core.filters.FilterException
import org.web3j.protocol.core.methods.request.EthFilter
import java.io.IOException
import java.util.*

class TokenWatcher(
    private val web3j: Web3j,
    private val fromAddress: String,
    tokenAddress: String
) {
    private val tokenContract = MainModule.provideTokenContract(
        tokenAddress,
        web3j,
        MainModule.provideReadOnlyTransactionManager(web3j, fromAddress)
    )

    private fun createFilter(): EthFilter {
        val transferEvent = Event(
            "Transfer",
            listOf<TypeReference<*>>(
                TypeReference.create(Address::class.java),
                TypeReference.create(Address::class.java),
                TypeReference.create(Uint::class.java)
            )
        )
        val encodedEvent = EventEncoder.encode(transferEvent)
        return EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            fromAddress.lowercase(Locale.getDefault())
        ).addSingleTopic(encodedEvent)
    }

    private fun parseTransferEvent(log: Log): TransferEvent = TransferEvent(
        log.topics[1].lowercase(),
        log.topics[2].lowercase(),
        log.data.toBigInteger()
    )


//    fun getTransactions(): Flow<TransferEvent> = flow {
//        val filter = createFilter()
//        try {
//            web3j.ethLogFlowable(filter)
//                .take(50)
//                .subscribe(/* onNext = */ { log ->
//                    val event = parseTransferEvent(log)
//                    this@flow.emit(
//                        TransferEvent(
//                            event.from,
//                            event.to,
//                            event.amount
//                        )
//                    )
//                },
//                    /* onError = */ { e -> e.printStackTrace() }
//                )
//        } catch (e: FilterException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
}