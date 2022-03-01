package org.mvnsearch.aerondemo;

import reactor.aeron.AeronDuplex;
import reactor.aeron.AeronInbound;
import reactor.aeron.AeronOutbound;

import java.util.function.Consumer;

/**
 * Aeron bound handler
 *
 * @author linux_china
 */
public interface AeronBoundHandler<T> {

    /**
     * Aeron message handler
     *
     * @param aeronConnection Aeron connection
     * @param inbound         in bound
     * @param outbound        out bound
     * @return message handler
     */
    Consumer<T> execute(AeronDuplex<?> aeronDuplex, AeronInbound<?> inbound, AeronOutbound outbound);
}
