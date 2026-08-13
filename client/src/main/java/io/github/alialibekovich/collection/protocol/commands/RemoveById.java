package io.github.alialibekovich.collection.protocol.commands;

import io.github.alialibekovich.collection.client.commands.ClientCommand;
import io.github.alialibekovich.collection.client.core.Receiver;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class RemoveById extends ClientCommand {
    transient private Receiver receiver;
    private static final long serialVersionUID = 32L;


    public RemoveById(Receiver receiver) {
        this.receiver = receiver;
    }

    public RemoveById() {

    }

    @Override
    public String aboutCommand() {
        return ("О команде 'remove_by_id': удаляется элемент из коллекции по его id.\n");
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Неправильный формат команды! Команда не будет выполнена.");
        } else {
            receiver.remove_by_id(args[1]);
        }
    }

    @Override
    public void executeForScript(String[] args, Scanner sc) {

    }
}
