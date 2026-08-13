package io.github.alialibekovich.collection.protocol.commands;

import io.github.alialibekovich.collection.client.core.Receiver;
import io.github.alialibekovich.collection.client.commands.ClientCommand;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class Login extends ClientCommand {
    transient private Receiver receiver;
    private static final long serialVersionUID = 32L;

    public Login(Receiver receiver) {
        this.receiver = receiver;
    }

    public Login() {
    }

    @Override
    public String aboutCommand() {
        return ("");
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Неправильный формат команды! Команда не будет выполнена.");
        } else {
            receiver.login(args[1], args[2]);
        }
    }

    @Override
    public void executeForScript(String[] args, Scanner sc) {
    }
}