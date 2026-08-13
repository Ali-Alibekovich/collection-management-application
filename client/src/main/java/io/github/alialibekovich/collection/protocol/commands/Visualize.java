package io.github.alialibekovich.collection.protocol.commands;

import io.github.alialibekovich.collection.client.core.Receiver;
import io.github.alialibekovich.collection.client.commands.ClientCommand;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class Visualize extends ClientCommand {
    transient private Receiver receiver;
    private static final long serialVersionUID = 32L;

    public Visualize(Receiver receiver) {
        this.receiver = receiver;
    }

    public Visualize() {
    }

    @Override
    public String aboutCommand() {
        return ("О команде 'visualize': визуализируется коллекция организаций.\n");
    }

    @Override
    public void execute(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        if (args.length != 1) {
            System.out.println("Неправильный формат команды! Команда не будет выполнена.");
        } else {
            receiver.visualize();
        }
    }

    @Override
    public void executeForScript(String[] args, Scanner sc) {

    }
}