package io.github.alialibekovich.collection.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;

/**
 * Deserializes one datagram (its own copy of the bytes), lets the dispatcher
 * produce the answer and sends it back. Transport concerns live here; the
 * handlers never touch the channel.
 */
public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final byte[] datagram;
    private final SocketAddress socketAddress;
    private final DatagramChannel channel;
    private final CommandDispatcher dispatcher;

    public RequestHandler(byte[] datagram, SocketAddress socketAddress,
                          DatagramChannel channel, CommandDispatcher dispatcher) {
        this.datagram = datagram;
        this.socketAddress = socketAddress;
        this.channel = channel;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(datagram))) {
            String answer = dispatcher.dispatch(in.readObject());
            if (answer != null) {
                channel.send(ByteBuffer.wrap(answer.getBytes()), socketAddress);
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            log.error("Failed to process a datagram from {}", socketAddress, e);
        }
    }
}
