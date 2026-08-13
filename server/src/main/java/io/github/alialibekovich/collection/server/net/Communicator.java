package io.github.alialibekovich.collection.server.net;

import io.github.alialibekovich.collection.server.db.DatabaseCommunicator;
import io.github.alialibekovich.collection.server.util.CollectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;

/**
 * UDP accept loop: receives a datagram, deserializes the command envelope and
 * hands it over to a {@link RequestHandler} thread.
 */
public class Communicator {

    private static final Logger log = LoggerFactory.getLogger(Communicator.class);

    private static final int BUFFER_SIZE = 4096;

    public void run(int port) {
        try {
            CollectionManager.initializeCollection();
            DatabaseCommunicator.getOrganizations().loadCollection(CollectionManager.getCollection());
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            datagramChannel.socket().bind(new InetSocketAddress(port));
            log.info("Server is listening on UDP port {}", port);
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                SocketAddress socketAddress = datagramChannel.receive(ByteBuffer.wrap(buffer));
                if (socketAddress == null) {
                    continue;
                }
                try {
                    RequestHandler handler = new RequestHandler(
                            new ObjectInputStream(new ByteArrayInputStream(buffer)),
                            socketAddress,
                            new CommandDecoder(datagramChannel, socketAddress));
                    handler.start();
                    Thread.sleep(50);
                } catch (EOFException | SocketException e) {
                    log.warn("Malformed datagram from {}", socketAddress, e);
                }
            }
        } catch (IOException e) {
            log.error("Server socket failure", e);
        } catch (SQLException e) {
            log.error("Failed to load the collection from the database", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("Server loop interrupted, shutting down");
        }
    }
}
