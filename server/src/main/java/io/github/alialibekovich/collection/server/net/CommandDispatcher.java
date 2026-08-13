package io.github.alialibekovich.collection.server.net;

import io.github.alialibekovich.collection.protocol.Command;
import io.github.alialibekovich.collection.protocol.SerializedArgumentCommand;
import io.github.alialibekovich.collection.protocol.SerializedCombinedCommand;
import io.github.alialibekovich.collection.protocol.SerializedObjectCommand;
import io.github.alialibekovich.collection.protocol.SerializedSimplyCommand;
import io.github.alialibekovich.collection.server.handlers.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

/**
 * Unwraps a command envelope and routes it to the registered handler
 * (Strategy). Adding a wire command means registering one more handler — no
 * dispatch code changes.
 */
public class CommandDispatcher {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);

    private final Map<Class<? extends Command>, CommandHandler> handlers;

    public CommandDispatcher(Map<Class<? extends Command>, CommandHandler> handlers) {
        this.handlers = Map.copyOf(handlers);
    }

    /** @return the answer to send back, or {@code null} if the envelope is unknown */
    public String dispatch(Object envelope) throws SQLException {
        if (envelope instanceof SerializedSimplyCommand simply) {
            return route(simply.getCommand(), "", null);
        }
        if (envelope instanceof SerializedArgumentCommand argument) {
            return route(argument.getCommand(), argument.getArg(), null);
        }
        if (envelope instanceof SerializedObjectCommand object) {
            return route(object.getCommand(), "", object.getObject());
        }
        if (envelope instanceof SerializedCombinedCommand combined) {
            return route(combined.getCommand(), combined.getArg(), combined.getObject());
        }
        log.warn("Unknown envelope type: {}", envelope == null ? "null" : envelope.getClass().getName());
        return null;
    }

    private String route(Command command, String arg, Object payload) throws SQLException {
        CommandHandler handler = handlers.get(command.getClass());
        if (handler == null) {
            log.warn("No handler registered for command {}", command.getClass().getSimpleName());
            return null;
        }
        return handler.handle(arg, payload);
    }
}
