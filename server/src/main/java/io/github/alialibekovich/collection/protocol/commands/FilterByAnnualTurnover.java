package io.github.alialibekovich.collection.protocol.commands;

import io.github.alialibekovich.collection.server.commands.ServerCommand;
import io.github.alialibekovich.collection.server.net.Receiver;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

public class FilterByAnnualTurnover extends ServerCommand {
    private static final long serialVersionUID = 32L;

    @Override
    public void execute(Object o, DatagramChannel channel, SocketAddress socketAddress) throws IOException {
        Receiver commandReceiver = new Receiver(channel);
        commandReceiver.filter_by_annual_turnover((String) o,socketAddress);
    }
}
