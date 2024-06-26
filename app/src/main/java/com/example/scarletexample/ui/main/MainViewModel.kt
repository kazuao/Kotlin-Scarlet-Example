package com.example.scarletexample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scarletexample.sockets.CoinbaseService
import com.example.scarletexample.sockets.modules.Subscribe
import com.example.scarletexample.utils.Crypto
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(service: CoinbaseService) : ViewModel() {

    private val _prices = MutableLiveData<Map<String, String?>>()
    val prices: LiveData<Map<String, String?>> = _prices

    private val combinedPrices = mutableMapOf<String, String?>().also { prices ->
        Crypto.entries.forEach { crypto ->
            prices[crypto.id]
        }
    }

    init {
        service.observeWebSocket()
            .flowOn(Dispatchers.IO)
            .onEach { event ->
                if (event !is WebSocket.Event.OnMessageReceived) {
                    Timber.d("Event = ${event::class.java.simpleName}!")
                }

                if (event is WebSocket.Event.OnConnectionOpened<*>) {
                    service.sendSubscribe(
                        Subscribe(
                            productIds = Crypto.entries.map { it.id },
                            channels = listOf("ticker")
                        )
                    )
                }
            }
            .launchIn(viewModelScope)

        service.observeTicker()
            .flowOn(Dispatchers.IO)
            .onEach {
                combinedPrices[it.productId] = it.price
                _prices.postValue(combinedPrices)
            }
            .launchIn(viewModelScope)
    }
}