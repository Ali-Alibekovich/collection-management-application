package io.github.alialibekovich.collection.protocol;

/**
 * Envelope for a command that carries both a payload object and a string argument
 * (e.g. {@code update}: the new element plus credentials and the target id).
 */
public class SerializedCombinedCommand implements java.io.Serializable {
    private static final long serialVersionUID = 32L;

    private final Command command;
    private final Object object;
    private final String arg;

    public SerializedCombinedCommand(Command command, Object object, String arg) {
        this.command = command;
        this.object = object;
        this.arg = arg;
    }

    public Command getCommand() {
        return command;
    }

    public Object getObject() {
        return object;
    }

    public String getArg() {
        return arg;
    }
}
