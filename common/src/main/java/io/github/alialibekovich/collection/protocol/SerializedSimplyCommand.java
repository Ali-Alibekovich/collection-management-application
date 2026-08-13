package io.github.alialibekovich.collection.protocol;

/**
 * Envelope for a command that carries no payload (e.g. {@code help}-like requests).
 */
public class SerializedSimplyCommand implements java.io.Serializable {
    private static final long serialVersionUID = 32L;

    private final Command command;

    public SerializedSimplyCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
