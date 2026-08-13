package io.github.alialibekovich.collection.server;

import io.github.alialibekovich.collection.protocol.SerializedArgumentCommand;
import io.github.alialibekovich.collection.protocol.commands.Login;
import io.github.alialibekovich.collection.protocol.commands.Register;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end concurrency test: boots the real server (separate JVM, real
 * PostgreSQL) and fires concurrent register/login datagrams from many client
 * sockets at once.
 *
 * <p>Guards the dispatch pipeline as a whole: every datagram must be processed
 * from its own copy of the receive buffer (the old shared-buffer loop lost or
 * corrupted packets under load), and the repositories must hold up when hit
 * from many worker threads.</p>
 */
@Testcontainers(disabledWithoutDocker = true)
class ServerConcurrencyIT {

    private static final int CLIENTS = 24;

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse(System.getProperty("postgres.image", "postgres:16-alpine"))
                    .asCompatibleSubstituteFor("postgres"));

    private static Process server;
    private static int port;

    @BeforeAll
    static void startServer() throws Exception {
        try (DatagramSocket probe = new DatagramSocket(0)) {
            port = probe.getLocalPort();
        }
        String javaBin = System.getProperty("java.home") + "/bin/java";
        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", System.getProperty("java.class.path"),
                Main.class.getName(), String.valueOf(port));
        builder.environment().put("DB_URL", POSTGRES.getJdbcUrl());
        builder.environment().put("DB_USER", POSTGRES.getUsername());
        builder.environment().put("DB_PASSWORD", POSTGRES.getPassword());
        builder.redirectErrorStream(true);
        server = builder.start();
        awaitServerListening();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.destroyForcibly();
        }
    }

    @Test
    void concurrentRegistrationsAndLoginsAllSucceed() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(CLIENTS);
        try {
            CountDownLatch start = new CountDownLatch(1);
            List<Future<String>> results = new ArrayList<>();
            for (int i = 0; i < CLIENTS; i++) {
                int client = i;
                results.add(pool.submit(() -> {
                    start.await();
                    try (DatagramSocket socket = new DatagramSocket()) {
                        socket.setSoTimeout(15_000);
                        String user = "user" + client;
                        String password = "password" + client;
                        String color = String.format("0x%06xff", client);

                        String registered = call(socket, new SerializedArgumentCommand(
                                new Register(), user + " " + password + " " + user + "@example.com " + color));
                        if (!"Успешно!".equals(registered)) {
                            return "register failed for " + user + ": " + registered;
                        }
                        String loggedIn = call(socket, new SerializedArgumentCommand(
                                new Login(), user + " " + password));
                        if (!loggedIn.startsWith("Успешно!")) {
                            return "login failed for " + user + ": " + loggedIn;
                        }
                        if (!loggedIn.endsWith(color)) {
                            return "wrong colour for " + user + ": " + loggedIn;
                        }
                        return "ok";
                    }
                }));
            }
            start.countDown();
            List<String> failures = new ArrayList<>();
            for (Future<String> result : results) {
                String outcome = result.get(60, TimeUnit.SECONDS);
                if (!"ok".equals(outcome)) {
                    failures.add(outcome);
                }
            }
            assertEquals(List.of(), failures);
        } finally {
            pool.shutdownNow();
        }
    }

    private static String call(DatagramSocket socket, SerializedArgumentCommand command) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(command);
        }
        byte[] payload = bytes.toByteArray();
        socket.send(new DatagramPacket(payload, payload.length, InetAddress.getLoopbackAddress(), port));

        byte[] buffer = new byte[4096];
        DatagramPacket answer = new DatagramPacket(buffer, buffer.length);
        socket.receive(answer);
        return new String(answer.getData(), 0, answer.getLength()).trim();
    }

    private static void awaitServerListening() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        long deadline = System.currentTimeMillis() + 30_000;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("Server is listening on UDP port")) {
                // keep draining stdout so the child never blocks on a full pipe
                Thread drainer = new Thread(() -> {
                    try {
                        while (reader.readLine() != null) {
                            // discard
                        }
                    } catch (IOException ignored) {
                        // process exited
                    }
                });
                drainer.setDaemon(true);
                drainer.start();
                return;
            }
            if (System.currentTimeMillis() > deadline) {
                break;
            }
        }
        assertTrue(false, "server did not start listening in time");
    }
}
