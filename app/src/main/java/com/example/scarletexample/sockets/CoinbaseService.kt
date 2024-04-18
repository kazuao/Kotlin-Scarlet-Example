package com.example.scarletexample.sockets

import com.example.scarletexample.sockets.modules.Subscribe
import com.example.scarletexample.sockets.modules.Ticker
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow

interface CoinbaseService {
    @Receive
    fun observeWebSocket(): Flow<WebSocket.Event>

    @Send
    fun sendSubscribe(subscribe: Subscribe)

    @Receive
    fun observeTicker(): Flow<Ticker>
}