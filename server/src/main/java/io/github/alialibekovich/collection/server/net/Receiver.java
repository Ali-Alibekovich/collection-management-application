package io.github.alialibekovich.collection.server.net;

import io.github.alialibekovich.collection.server.db.DatabaseCommunicator;
import io.github.alialibekovich.collection.server.db.OrganizationsRepository;
import io.github.alialibekovich.collection.server.util.CollectionManager;
import io.github.alialibekovich.collection.server.util.CollectionUtils;
import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.server.util.MessageMailSender;
import io.github.alialibekovich.collection.server.util.ParserJson;

import javax.mail.MessagingException;
import java.io.*;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;

import static java.nio.ByteBuffer.wrap;

public class Receiver {
    private DatagramChannel channel;

    public Receiver(DatagramChannel channel) {
        this.channel = channel;
    }

    public void info(String arg, SocketAddress socketAddress) throws IOException {
        String[] arr = arg.split(" ");
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            byte[] answer = CollectionManager.information().getBytes();
            channel.send(wrap(answer), socketAddress);
        } else {
            channel.send(wrap("Пользователь не прошел проверку".getBytes()), socketAddress);
        }
    }

    public void show(String arg, SocketAddress socketAddress) throws IOException, SQLException {
        String[] arr = arg.split(" ");
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            OrganizationsRepository.loadCollection(CollectionManager.getCollection());
            byte[] answer = ParserJson.toJson(CollectionManager.getCollection()).getBytes();
            channel.send(wrap(answer), socketAddress);
        } else {
            channel.send(wrap("Пользователь не прошел проверку".getBytes()), socketAddress);
        }
    }
    public void visualize(String arg, SocketAddress socketAddress) throws IOException, SQLException {
        String[] arr = arg.split(" ");
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            OrganizationsRepository.loadCollection(CollectionManager.getCollection());
            byte[] answer = ParserJson.toJson(CollectionManager.getCollection()).getBytes();
            channel.send(wrap(answer), socketAddress);
        } else {
            channel.send(wrap("Пользователь не прошел проверку".getBytes()), socketAddress);
        }
    }

    public void add(Object o, String arg, SocketAddress socketAddress) throws IOException {
        String[] arr = arg.split(" ");
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            Organization organization = (Organization) o;
            DatabaseCommunicator.getOrganizations().addOrganizationToTheBase(organization, -1);
            byte[] answer = CollectionManager.addOrganization().getBytes();
            channel.send(wrap(answer), socketAddress);
        } else {
            channel.send(wrap("Пользователь не прошел проверку".getBytes()), socketAddress);
        }
    }

    public void update(String arg, Object o, SocketAddress socketAddress) throws IOException {
        String[] arr = arg.split(" ");
        byte[] answer = new byte[0];
        int ID;
        ID = Integer.parseInt(arr[2]);
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            if (OrganizationsRepository.isOwnedBy(ID, arr[0])) {
                try {
                    if (CollectionUtils.doesExist(ID)) {
                        Organization organization = (Organization) o;
                        DatabaseCommunicator.getOrganizations().deleteOrganizationFromDataBase(ID);
                        DatabaseCommunicator.getOrganizations().addOrganizationToTheBase(organization, ID);
                        CollectionManager.updateElement((Organization) o, ID);
                        answer = "Организация обновлена.".getBytes();
                    } else {
                        answer = "В коллекции нет организации с таким ID.".getBytes();
                    }
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                    answer = "ID был введён некорректно. Команда не выполнена.".getBytes();
                } finally {
                    channel.send(wrap(answer), socketAddress);
                }
            } else {
                channel.send(wrap("Данный объект вам не принадлежит либо его не существует.".getBytes()), socketAddress);
            }
        } else {
            channel.send(wrap("Пользователь не прошел проверку.".getBytes()), socketAddress);
        }
    }

    public void remove_by_id(String arg, SocketAddress socketAddress) throws IOException {
        String[] arr = arg.split(" ");
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            byte[] answer = new byte[0];
            int ID;
            try {
                ID = Integer.parseInt(arr[2]);
                if (OrganizationsRepository.isOwnedBy(ID, arr[0])) {
                    if (CollectionUtils.doesExist(ID)) {
                        DatabaseCommunicator.getOrganizations().deleteOrganizationFromDataBase(ID);
                        CollectionManager.removeElement(ID);
                        answer = "Элемент удалён.".getBytes();
                    } else {
                        answer = "Такого элемента нет в коллекции.".getBytes();
                    }
                } else {
                    answer = "Данный объект не принадлежит вам.".getBytes();
                }
            } catch (NumberFormatException | SQLException e) {
                answer = "Неправильный аргумент команды! Команда не будет выполнена.".getBytes();
            } finally {
                channel.send(wrap(answer), socketAddress);
            }
        } else {
            channel.send(wrap("Пользователь не прошел проверку.".getBytes()), socketAddress);
        }
    }

    public void clear(String arg, SocketAddress socketAddress) throws IOException {
        String[] arr = arg.split(" ");
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            CollectionManager.clearCollectionOnDataBase(arr[0]);
            byte[] answer = "Коллекция очищена".getBytes();
            channel.send(wrap(answer), socketAddress);
        } else {
            channel.send(wrap("Пользователь не прошел проверку.".getBytes()), socketAddress);
        }
    }

    public void remove_head(String arg, SocketAddress socketAddress) throws IOException {
        String[] arr = arg.split(" ");
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            byte[] answer = CollectionManager.removeHead(arr[0]).getBytes();
            channel.send(wrap(answer), socketAddress);
        } else {
            channel.send(wrap("Пользователь не прошел проверку.".getBytes()), socketAddress);
        }
    }

    public void add_if_min(String arg, Object o, SocketAddress socketAddress) throws IOException {
        String[] arr = arg.split(" ");
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            byte[] answer = CollectionManager.addIfMin((Organization) o).getBytes();
            channel.send(wrap(answer), socketAddress);
        } else {
            channel.send(wrap("Пользователь не прошел проверку.".getBytes()), socketAddress);
        }
    }

    public void filter_by_annual_turnover(String arg, SocketAddress socketAddress) throws IOException {
        String[] arr = arg.split(" ");
        byte[] answer = new byte[0];
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            try {
                answer = CollectionManager.filterByAnnualTurnover(Double.parseDouble(arr[2])).getBytes();
            } catch (NumberFormatException e) {
                answer = "Неправильный аргумент команды! Команда не будет выполнена.".getBytes();
            } finally {
                channel.send(wrap(answer), socketAddress);
            }
        } else {
            channel.send(wrap("Пользователь не прошел проверку.".getBytes()), socketAddress);
        }
    }

    public void filter_starts_with_name(String arg, SocketAddress socketAddress) throws IOException {
        String[] arr = arg.split(" ");
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            byte[] answer = CollectionManager.filterStartsWithName(arr[2]).getBytes();
            channel.send(wrap(answer), socketAddress);
        } else {
            channel.send(wrap("Пользователь не прошел проверку.".getBytes()), socketAddress);
        }
    }

    public void login(String arg, SocketAddress socketAddress) throws IOException, SQLException {
        String[] arr = arg.split(" ");
        String resultForSending = DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])
                ? "Успешно!"
                : "Не_успешно!";
        resultForSending = resultForSending.concat(" " + DatabaseCommunicator.getUsers().getColor(arr[0]));
        channel.send(wrap(resultForSending.getBytes()), socketAddress);
    }

    public void register(String arg, SocketAddress socketAddress) throws IOException, SQLException {
        String[] arr = arg.split(" ");
        String resultForSending;
        if (!DatabaseCommunicator.getUsers().loginExists(arr[0]) && !DatabaseCommunicator.getUsers().colorTaken(arr[3])) {
            DatabaseCommunicator.getUsers().addUser(arr[0], arr[1], arr[3]);
            try {
                MessageMailSender.sendWelcome(arr[0], arr[2]);
                resultForSending = "Успешно!";
            } catch (MessagingException e) {
                resultForSending = "Успешно, но приветственное письмо не было отправлено из-за неполадок с подключением!";
            }
        } else resultForSending = "Пользователь с данным логином уже зарегистрирован!";
        channel.send(wrap(resultForSending.getBytes()), socketAddress);
    }

    public void print_field_descending_annual_turnover(String arg, SocketAddress socketAddress) throws IOException {
        String[] arr = arg.split(" ");
        if (DatabaseCommunicator.getUsers().checkCredentials(arr[0], arr[1])) {
            byte[] answer = CollectionManager.printFieldDescendingAnnualTurnover().getBytes();
            channel.send(wrap(answer), socketAddress);
        } else {
            channel.send(wrap("Пользователь не прошел проверку.".getBytes()), socketAddress);
        }
    }

}