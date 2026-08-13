package io.github.alialibekovich.collection.protocol;

/**
 * Envelope for a command that carries a single string argument
 * (credentials, identifiers, filter values and so on).
 */
public class SerializedArgumentCommand implements java.io.Serializable {
    private static final long serialVersionUID = 32L;

    private final Command command;
    private final String arg;

    public SerializedArgumentCommand(Command command, String arg) {
        this.command = command;
        this.arg = arg;
    }

    public Command getCommand() {
        return command;
    }

    public String getArg() {
        return arg;
    }
}
