package io.github.alialibekovich.collection.protocol.commands;

import io.github.alialibekovich.collection.client.commands.ClientCommand;
import io.github.alialibekovich.collection.client.core.Receiver;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class Clear extends ClientCommand {
    transient private Receiver receiver;
    private static final long serialVersionUID = 32L;

    public Clear(Receiver receiver) {
        this.receiver = receiver;
    }

    public Clear() {

    }

    @Override
    public String aboutCommand() {
        return ("О команде 'clear': очистка коллекции.\n");
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Неправильный формат команды! Команда не будет выполнена.");
        } else {
            receiver.clear();
        }
    }

    @Override
    public void executeForScript(String[] args, Scanner sc) {

    }
}