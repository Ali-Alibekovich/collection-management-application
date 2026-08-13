package io.github.alialibekovich.collection.protocol.commands;

import io.github.alialibekovich.collection.client.commands.ClientCommand;
import io.github.alialibekovich.collection.client.core.Receiver;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class Add extends ClientCommand {
    transient private Receiver receiver;
    private static final long serialVersionUID = 32L;


    public Add(Receiver receiver) {
        this.receiver = receiver;
    }

    public Add() {

    }

    @Override
    public String aboutCommand() {
        return ("О команде 'add': в коллекцию добавляется новый элемент.\n");//TODO Этой и подобным командам написать, что доступ через show.
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Неправильный формат команды! Команда не будет выполнена.");
        } else {
            Scanner sc = new Scanner(System.in);
            receiver.add(sc);
        }
    }

    @Override
    public void executeForScript(String[] args, Scanner sc) {
        if (args.length > 1) {
            System.out.println("Неправильный формат команды! Команда не будет выполнена.");
        } else {
            try {
                receiver.add(sc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
