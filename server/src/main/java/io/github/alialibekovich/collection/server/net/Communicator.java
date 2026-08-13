package io.github.alialibekovich.collection.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UDP accept loop: receives a datagram, hands its bytes to a worker thread.
 *
 * <p>The channel stays in blocking mode — with a single channel a blocking
 * {@code receive} is simpler than a {@code Selector} and burns no CPU while
 * idle. Every datagram is copied out of the receive buffer before dispatch,
 * so workers never share the buffer with the accept loop.</p>
 */
public class Communicator {

    private static final Logger log = LoggerFactory.getLogger(Communicator.class);

    private static final int BUFFER_SIZE = 4096;
    private static final int WORKER_THREADS = Math.max(2, Runtime.getRuntime().availableProcessors());

    private final CommandDispatcher dispatcher;

    public Communicator(CommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void run(int port) {
        ExecutorService workers = Executors.newFixedThreadPool(WORKER_THREADS);
        try {
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.socket().bind(new InetSocketAddress(port));
            log.info("Server is listening on UDP port {} ({} worker threads)", port, WORKER_THREADS);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (true) {
                buffer.clear();
                SocketAddress socketAddress = datagramChannel.receive(buffer);
                if (socketAddress == null) {
                    continue;
                }
                buffer.flip();
                byte[] datagram = new byte[buffer.remaining()];
                buffer.get(datagram);
                workers.submit(new RequestHandler(datagram, socketAddress, datagramChannel, dispatcher));
            }
        } catch (IOException e) {
            log.error("Server socket failure", e);
        } finally {
            workers.shutdown();
        }
    }
}
