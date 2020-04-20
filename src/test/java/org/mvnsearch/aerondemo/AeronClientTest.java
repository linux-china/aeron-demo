package org.mvnsearch.aerondemo;

import org.agrona.DirectBuffer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import reactor.aeron.AeronDuplex;
import reactor.aeron.mdc.AeronClient;
import reactor.aeron.mdc.AeronResources;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Aeron client test
 *
 * @author linux_china
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AeronClientTest {
    AeronResources resources;

    @BeforeAll
    public void setUp() {
        resources = new AeronResources().useTmpDir().start().block();
    }

    @AfterAll
    public void tearDown() {
        resources.dispose();
    }

    @Test
    public void testSend() throws Exception {
        AeronDuplex<DirectBuffer> client = createConnection(connection ->
                connection.outbound()
                        .sendString(Flux.fromStream(Stream.of("Hello", "world!")).log("send"))
                        .onDispose(resources));
        Thread.sleep(1000);
        client.dispose();
        Thread.sleep(1000);
    }

    private AeronDuplex<DirectBuffer> createConnection(
            Function<? super AeronDuplex<DirectBuffer>, ? extends Publisher<Void>> handler) {
        return AeronClient.create(resources)
                .options("localhost", 13000, 13001)
                .handle(handler)
                .connect()
                .block(Duration.ofSeconds(10));
    }
}
