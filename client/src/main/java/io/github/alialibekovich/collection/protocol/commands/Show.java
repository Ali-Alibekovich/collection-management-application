package io.github.alialibekovich.collection.protocol.commands;

import io.github.alialibekovich.collection.client.commands.ClientCommand;
import io.github.alialibekovich.collection.client.core.Receiver;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class Show extends ClientCommand {
    transient private Receiver receiver;
    private static final long serialVersionUID = 32L;

    public Show(Receiver receiver) {
        this.receiver = receiver;
    }

    public Show() {

    }

    @Override
    public String aboutCommand() {
        return ("О команде 'show': выводятся все элементы коллекции в виде таблички.\n");
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Неправильный формат команды! Команда не будет выполнена.");
        } else {
            receiver.show();
        }
    }

    @Override
    public void executeForScript(String[] args, Scanner sc) {

    }
}
