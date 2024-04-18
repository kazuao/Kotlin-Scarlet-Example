package com.example.scarletexample.sockets

import android.os.Build
import androidx.annotation.RequiresApi
import com.tinder.scarlet.Stream
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.utils.getRawType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.lang.IllegalArgumentException
import java.lang.reflect.Type
import kotlinx.coroutines.flow.Flow

class FlowStreamAdapter<T> : StreamAdapter<T, Flow<T>> {
    override fun adapt(stream: Stream<T>) = callbackFlow<T> {
        stream.start(object : Stream.Observer<T> {
            override fun onComplete() {
                close()
            }

            override fun onError(throwable: Throwable) {
                close(cause = throwable)
            }

            override fun onNext(data: T) {
                if (!isClosedForSend) trySend(data).isSuccess
            }

        })
        awaitClose {}
    }

    class Factory: StreamAdapter.Factory {
        @RequiresApi(Build.VERSION_CODES.R)
        override fun create(type: Type): StreamAdapter<Any, Any> {
            return when (type.getRawType()) {
                Flow::class.java -> FlowStreamAdapter()
                else -> throw IllegalArgumentException()
            }
        }
    }
}