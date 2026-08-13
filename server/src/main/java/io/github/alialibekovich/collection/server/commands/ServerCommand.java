package io.github.alialibekovich.collection.server.commands;

import io.github.alialibekovich.collection.protocol.Command;

/**
 * Server-side base of the twin command classes. Commands arrive as data: the
 * behaviour lives in the handler registered for the command's class (see
 * {@code CommandDispatcher}), so the twins carry no logic of their own.
 */
public abstract class ServerCommand implements Command {
}
