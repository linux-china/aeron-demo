package org.mvnsearch.aerondemo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.aeron.AeronClient;
import reactor.aeron.AeronConnection;
import reactor.aeron.AeronResources;
import reactor.core.publisher.Flux;

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
        AeronConnection client = AeronClient.create(resources)
                .options("localhost", 13000, 13001)
                .handle(
                        connection1 -> {
                            System.out.println("Handler invoked");
                            return connection1
                                    .outbound()
                                    .sendString(Flux.fromStream(Stream.of("Hello", "world!")).log("send"))
                                    .then(connection1.onDispose());
                        })
                .connect()
                .block();
        Thread.sleep(1000);
    }
}
