package io.github.alialibekovich.collection.client.commands;

import io.github.alialibekovich.collection.protocol.Command;

import java.io.IOException;
import java.util.Scanner;

/**
 * Base class for the client-side representation of every command.
 *
 * <p>{@link #execute(String[])} validates user input and delegates to the
 * {@code Receiver}, which performs the actual request. Commands that may appear
 * inside scripts additionally implement {@link #executeForScript(String[], Scanner)}.</p>
 */
public abstract class ClientCommand implements Command {

    /** @return a human-readable description shown by the {@code help} command */
    public abstract String aboutCommand();

    public abstract void execute(String[] args) throws IOException, InterruptedException, ClassNotFoundException;

    public abstract void executeForScript(String[] args, Scanner sc);
}
