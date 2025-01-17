package org.example.service.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerService {

    private final FileService fileService;
    private final String ADDRESS;
    private final String PORT;

    int numOfThreads = Math.min(4, Runtime.getRuntime().availableProcessors());
    private final ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);


    public ServerService(final Properties properties) {
        ADDRESS = properties.getProperty("address");
        PORT = properties.getProperty("port");
        fileService = new FileService(properties.getProperty("server_storage"), properties.getProperty("db_path"));
    }


    public final void start() {

        try (ServerSocket server = new ServerSocket(Integer.parseInt(PORT), 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started!");

            while (!fileService.isExit()) {
                executor.submit(new Session(server.accept(), fileService));
            }

        } catch (IOException exception) {
            System.out.println("Server Error");
            exception.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
