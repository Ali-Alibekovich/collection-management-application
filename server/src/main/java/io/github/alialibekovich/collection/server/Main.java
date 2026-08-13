package io.github.alialibekovich.collection.server;

import io.github.alialibekovich.collection.server.db.DatabaseCommunicator;
import io.github.alialibekovich.collection.server.net.Communicator;
import io.github.alialibekovich.collection.server.util.MessageMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server entry point.
 *
 * <p>Usage: {@code java -jar collection-server.jar [port]} (default port {@value #DEFAULT_PORT}).
 * Database and mail settings are read from the environment, see {@link DatabaseCommunicator}
 * and {@link MessageMailSender}.</p>
 */
public final class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final int DEFAULT_PORT = 5555;

    private Main() {
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error("'{}' is not a valid port number", args[0]);
                System.exit(1);
            }
        }
        MessageMailSender.configureFromEnvironment();
        new DatabaseCommunicator().start();
        new Communicator().run(port);
    }
}
