package io.github.alialibekovich.collection.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.sql.SQLException;

/**
 * Deserializes one datagram and executes the command it carries.
 *
 * <p>Owns its private copy of the datagram bytes, so the accept loop is free
 * to reuse its receive buffer for the next packet.</p>
 */
public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final byte[] datagram;
    private final SocketAddress socketAddress;
    private final CommandDecoder decoder;

    public RequestHandler(byte[] datagram, SocketAddress socketAddress, CommandDecoder decoder) {
        this.datagram = datagram;
        this.socketAddress = socketAddress;
        this.decoder = decoder;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(datagram))) {
            decoder.decode(in.readObject());
        } catch (IOException | SQLException | ClassNotFoundException e) {
            log.error("Failed to process a datagram from {}", socketAddress, e);
        }
    }
}
