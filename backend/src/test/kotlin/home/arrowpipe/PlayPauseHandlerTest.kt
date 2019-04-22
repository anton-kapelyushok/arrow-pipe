package home.arrowpipe

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Flux
import java.net.URI
import java.time.Duration

@RunWith(SpringRunner::class)
@Ignore
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, value = "8080")
class PlayPauseHandlerTest {

    private val LOG = LoggerFactory.getLogger(PlayPauseHandlerTest::class.java)

    @Test
    fun sendMessage() {
        val client = ReactorNettyWebSocketClient()

        val url = URI("ws://localhost:8080/play-pause-controller")
        client.execute(url) { session ->
            val keepAlive = session.send(Flux.interval(Duration.ofMillis(1000L)).map { session.textMessage("keepAlive") })
            val receive = session.receive()
                    .doOnNext {
                        LOG.info(it.payloadAsText)
                    }.then()
            Flux.merge(keepAlive, receive).then()
        }.block()
    }
}
