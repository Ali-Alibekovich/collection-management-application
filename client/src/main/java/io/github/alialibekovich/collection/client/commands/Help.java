package io.github.alialibekovich.collection.client.commands;

import io.github.alialibekovich.collection.client.commands.ClientCommand;
import io.github.alialibekovich.collection.client.core.Receiver;

import java.util.Scanner;

public class Help extends ClientCommand {

    private final Receiver receiver;

    public Help(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public String aboutCommand() {
        return ("О команде 'help': выводится справка по доступным командам.\n");
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            System.out.println("Неправильный формат команды! Команда не будет выполнена.");
        } else {
            receiver.help();
        }
    }

    @Override
    public void executeForScript(String[] args, Scanner sc) {

    }
}
