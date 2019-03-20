package org.mvnsearch.aerondemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.aeron.AeronResources;
import reactor.aeron.AeronServer;
import reactor.aeron.OnDisposable;

/**
 * reactor Aeron auto configuration
 *
 * @author linux_china
 */
@Configuration
public class ReactorAeronAutoConfiguration {

    @Bean(destroyMethod = "dispose")
    public AeronResources aeronResources() {
        return new AeronResources().useTmpDir().start().block();
    }

    @Bean
    public AeronBoundHandler<String> aeronMessageHandler() {
        return (aeronConnection, inbound, outbound) -> message -> System.out.println(message);
    }

    @Bean
    public OnDisposable aeronServer(@Autowired AeronResources aeronResources, AeronBoundHandler<String> messageHandler) {
        return AeronServer.create(aeronResources)
                .options("localhost", 13000, 13001)
                .handle(aeronConnection -> aeronConnection
                        .inbound()
                        .receive()
                        .asString()
                        .doOnNext(messageHandler.execute(aeronConnection, aeronConnection.inbound(), aeronConnection.outbound()))
                        .then(aeronConnection.onDispose()))
                .bind()
                .block();
    }
}
