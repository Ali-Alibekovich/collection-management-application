package io.github.alialibekovich.collection.protocol.commands;

import io.github.alialibekovich.collection.server.commands.ServerCommand;
import io.github.alialibekovich.collection.protocol.SerializedCombinedCommand;
import io.github.alialibekovich.collection.server.net.Receiver;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;

public class Add extends ServerCommand {
    private static final long serialVersionUID = 32L;

    @Override
    public void execute(Object o, DatagramChannel channel, SocketAddress socketAddress) throws IOException, SQLException {
        SerializedCombinedCommand combinedCommand = (SerializedCombinedCommand) o;
        Object obj = combinedCommand.getObject();
        String arg = combinedCommand.getArg();
        Receiver commandReceiver = new Receiver(channel);
        commandReceiver.add(obj, arg, socketAddress);
    }
}
