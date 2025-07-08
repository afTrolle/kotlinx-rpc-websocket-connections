package com.aftrolle

import io.ktor.client.HttpClient
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.util.logging.Logger
import io.ktor.util.logging.debug
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import kotlin.time.Duration.Companion.seconds

@Rpc
interface Service { fun flow(): Flow<Int> }

class ServiceImpl(val logger: Logger) : Service {
    override fun flow(): Flow<Int> = flow {
        var counter = 0
        while (true) {
            emit(counter++)
            delay(100)
        }
    }.onCompletion {
        logger.debug { "on flow completion" }
    }
}

fun main() {
    embeddedServer(Netty, 8080) {
        server()
        client()
    }.start(wait = true)
}

private fun Application.server() {
    install(Krpc) { serialization { json() } }
    routing {
        rpc {
            log.debug("on krpcRoute - start")
            coroutineContext.job.invokeOnCompletion {
                log.debug("on krpcRoute - completion")
            }
            registerService<Service> {
                log.debug("on service")
                ServiceImpl(application.log)
            }
        }
    }
}

private fun Application.client() = launch {
    val ktorClient = HttpClient { installKrpc { serialization { json() } } }
    val rpcClient = ktorClient.rpc("ws://127.0.0.1:8080")
    while (true) {
        withTimeoutOrNull(2.seconds) {
            rpcClient.withService<Service>().flow().collect()
        }
        log.debug("restart collection")
    }
}
