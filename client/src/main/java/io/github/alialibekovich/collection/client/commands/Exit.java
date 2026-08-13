package io.github.alialibekovich.collection.client.commands;

import io.github.alialibekovich.collection.client.commands.ClientCommand;
import io.github.alialibekovich.collection.client.core.Receiver;

import java.util.Scanner;

public class Exit extends ClientCommand {

    private final Receiver receiver;

    public Exit(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public String aboutCommand() {
        return ("О команде 'exit': завершение программы.\n");
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            System.out.println("Неправильный формат команды! Команда не будет выполнена.");
        } else {
            receiver.exit();
        }
    }

    @Override
    public void executeForScript(String[] args, Scanner sc) {
    }
}