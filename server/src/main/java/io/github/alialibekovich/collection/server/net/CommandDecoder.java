package io.github.alialibekovich.collection.server.net;

import io.github.alialibekovich.collection.server.commands.ServerCommand;
import io.github.alialibekovich.collection.protocol.*;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;

public class CommandDecoder {
    private final DatagramChannel channel;
    private final SocketAddress socketAddress;

    public CommandDecoder(DatagramChannel channel, SocketAddress socketAddress) {
        this.channel = channel;
        this.socketAddress = socketAddress;
    }

    public void decode(Object o) throws IOException, SQLException {
        if (o instanceof SerializedSimplyCommand) {
            SerializedSimplyCommand simplyCommand = (SerializedSimplyCommand) o;
            ServerCommand command = (ServerCommand) simplyCommand.getCommand();
            String arg = "";
            command.execute(arg, channel, socketAddress);
        }

        if (o instanceof SerializedArgumentCommand) {
            SerializedArgumentCommand argumentCommand = (SerializedArgumentCommand) o;
            ServerCommand command = (ServerCommand) argumentCommand.getCommand();
            String arg = argumentCommand.getArg();
            command.execute(arg, channel, socketAddress);
        }

        if (o instanceof SerializedObjectCommand) {
            SerializedObjectCommand objectCommand = (SerializedObjectCommand) o;
            ServerCommand command = (ServerCommand) objectCommand.getCommand();
            Object arg = objectCommand.getObject();
            command.execute(arg, channel, socketAddress);
        }

        if (o instanceof SerializedCombinedCommand) {
            SerializedCombinedCommand combinedCommand = (SerializedCombinedCommand) o;
            ServerCommand command = (ServerCommand) combinedCommand.getCommand();
            command.execute(combinedCommand, channel, socketAddress);
        }
    }
}