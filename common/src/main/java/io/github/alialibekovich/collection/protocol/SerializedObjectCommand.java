package io.github.alialibekovich.collection.protocol;

/**
 * Envelope for a command that carries a serializable payload object.
 */
public class SerializedObjectCommand implements java.io.Serializable {
    private static final long serialVersionUID = 32L;

    private final Command command;
    private final Object object;

    public SerializedObjectCommand(Command command, Object object) {
        this.command = command;
        this.object = object;
    }

    public Command getCommand() {
        return command;
    }

    public Object getObject() {
        return object;
    }
}
