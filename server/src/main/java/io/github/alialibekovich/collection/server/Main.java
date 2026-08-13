package io.github.alialibekovich.collection.server;

import io.github.alialibekovich.collection.protocol.Command;
import io.github.alialibekovich.collection.protocol.commands.Add;
import io.github.alialibekovich.collection.protocol.commands.AddIfMin;
import io.github.alialibekovich.collection.protocol.commands.Clear;
import io.github.alialibekovich.collection.protocol.commands.FilterByAnnualTurnover;
import io.github.alialibekovich.collection.protocol.commands.FilterStartsWithName;
import io.github.alialibekovich.collection.protocol.commands.Info;
import io.github.alialibekovich.collection.protocol.commands.Login;
import io.github.alialibekovich.collection.protocol.commands.PrintFieldDescendingAnnualTurnover;
import io.github.alialibekovich.collection.protocol.commands.Register;
import io.github.alialibekovich.collection.protocol.commands.RemoveById;
import io.github.alialibekovich.collection.protocol.commands.RemoveHead;
import io.github.alialibekovich.collection.protocol.commands.Show;
import io.github.alialibekovich.collection.protocol.commands.Update;
import io.github.alialibekovich.collection.protocol.commands.Visualize;
import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.db.DatabaseConnector;
import io.github.alialibekovich.collection.server.db.OrganizationsRepository;
import io.github.alialibekovich.collection.server.db.UsersRepository;
import io.github.alialibekovich.collection.server.handlers.AddHandler;
import io.github.alialibekovich.collection.server.handlers.AddIfMinHandler;
import io.github.alialibekovich.collection.server.handlers.ClearHandler;
import io.github.alialibekovich.collection.server.handlers.CommandHandler;
import io.github.alialibekovich.collection.server.handlers.FilterByAnnualTurnoverHandler;
import io.github.alialibekovich.collection.server.handlers.FilterStartsWithNameHandler;
import io.github.alialibekovich.collection.server.handlers.InfoHandler;
import io.github.alialibekovich.collection.server.handlers.LoginHandler;
import io.github.alialibekovich.collection.server.handlers.PrintFieldDescendingAnnualTurnoverHandler;
import io.github.alialibekovich.collection.server.handlers.RegisterHandler;
import io.github.alialibekovich.collection.server.handlers.RemoveByIdHandler;
import io.github.alialibekovich.collection.server.handlers.RemoveHeadHandler;
import io.github.alialibekovich.collection.server.handlers.ShowHandler;
import io.github.alialibekovich.collection.server.handlers.UpdateHandler;
import io.github.alialibekovich.collection.server.net.CommandDispatcher;
import io.github.alialibekovich.collection.server.net.Communicator;
import io.github.alialibekovich.collection.server.util.WelcomeMailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Server entry point and composition root: reads the configuration, opens the
 * database, wires the object graph by hand and starts the accept loop.
 *
 * <p>Usage: {@code java -jar collection-server.jar [port]}
 * (default port {@value #DEFAULT_PORT}).</p>
 */
public final class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final int DEFAULT_PORT = 5555;

    private Main() {
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error("'{}' is not a valid port number", args[0]);
                System.exit(1);
            }
        }
        try {
            Connection connection = DatabaseConnector.connect();
            UsersRepository users = new UsersRepository(connection);
            OrganizationsRepository organizations = new OrganizationsRepository(connection);
            OrganizationCollection collection = new OrganizationCollection(organizations);
            collection.reload();
            WelcomeMailer mailer = WelcomeMailer.fromEnvironment();

            CommandDispatcher dispatcher = new CommandDispatcher(handlers(users, organizations, collection, mailer));
            new Communicator(dispatcher).run(port);
        } catch (SQLException e) {
            log.error("Failed to connect to the database", e);
            System.exit(1);
        }
    }

    private static Map<Class<? extends Command>, CommandHandler> handlers(UsersRepository users,
                                                                          OrganizationsRepository organizations,
                                                                          OrganizationCollection collection,
                                                                          WelcomeMailer mailer) {
        Map<Class<? extends Command>, CommandHandler> handlers = new HashMap<>();
        handlers.put(Login.class, new LoginHandler(users));
        handlers.put(Register.class, new RegisterHandler(users, mailer));
        handlers.put(Info.class, new InfoHandler(users, collection));
        ShowHandler show = new ShowHandler(users, collection);
        handlers.put(Show.class, show);
        handlers.put(Visualize.class, show);
        handlers.put(Add.class, new AddHandler(users, organizations, collection));
        handlers.put(Update.class, new UpdateHandler(users, organizations, collection));
        handlers.put(RemoveById.class, new RemoveByIdHandler(users, organizations, collection));
        handlers.put(Clear.class, new ClearHandler(users, organizations, collection));
        handlers.put(RemoveHead.class, new RemoveHeadHandler(users, collection));
        handlers.put(AddIfMin.class, new AddIfMinHandler(users, collection));
        handlers.put(FilterByAnnualTurnover.class, new FilterByAnnualTurnoverHandler(users, collection));
        handlers.put(FilterStartsWithName.class, new FilterStartsWithNameHandler(users, collection));
        handlers.put(PrintFieldDescendingAnnualTurnover.class,
                new PrintFieldDescendingAnnualTurnoverHandler(users, collection));
        return handlers;
    }
}
