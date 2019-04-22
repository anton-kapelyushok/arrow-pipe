package home.arrowpipe

import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

val LOG: Logger = LoggerFactory.getLogger(PlayPauseHandler::class.java)

@SpringBootApplication
class ArrowPipeApplication

fun main(args: Array<String>) {
    runApplication<ArrowPipeApplication>(*args)
}

@Component
class PlayPauseHandler(private val globalKeyListener: Flux<NativeKeyEvent>) : WebSocketHandler {
    override fun handle(session: WebSocketSession): Mono<Void> {
        LOG.info("Connected")

        val keepAlive = Flux.interval(Duration.ofMillis(5000L))
                .map {
                    session.textMessage("keepalive")
                }

        val keyStream = globalKeyListener
                .map {
                    when (it.rawCode) {
                        177 -> "prev"
                        176 -> "next"
                        179 -> "pause"
                        else -> "other"
                    }
                }
                .filter {
                    it != "other"
                }
                .doOnNext {
                    LOG.info("$it pressed")
                }
                .map {
                    session.textMessage(it)
                }

        return session.send(Flux.merge(keyStream, keepAlive))
    }
}

@Configuration
class GlobalKeyListenerConfig {
    @PostConstruct
    fun registerNativeHook() {
        GlobalScreen.registerNativeHook()
    }

    @PreDestroy
    fun unregisterNativeHook() {
        GlobalScreen.unregisterNativeHook()
    }

    @Bean
    fun globalKeyListener(): Flux<NativeKeyEvent> =
            Flux.create { emitter ->
                val listener = object : NativeKeyListener {
                    override fun nativeKeyTyped(e: NativeKeyEvent) = Unit

                    override fun nativeKeyPressed(e: NativeKeyEvent) {
                        emitter.next(e)
                    }

                    override fun nativeKeyReleased(e: NativeKeyEvent) = Unit
                }
                GlobalScreen.addNativeKeyListener(listener)
            }
}

@Configuration
class WebConfig {
    @Bean
    fun handlerMapping(playPauseHandler: PlayPauseHandler) =
            SimpleUrlHandlerMapping().apply {
                order = -1
                urlMap = mapOf("play-pause-controller" to playPauseHandler)
            }

    @Bean
    fun handlerAdapter() = WebSocketHandlerAdapter()
}
