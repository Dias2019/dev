package org.example.service.server;

import org.example.model.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Session extends Thread {
    private final Socket socket;
    private final FileService service;
    public Session(Socket socketPerClient, FileService fileService) {
        socket = socketPerClient;
        service = fileService;
    }


    public void handleClientRequest() throws IOException {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {

            Request msg = service.receiveRequest(in);
            switch (msg.getMethod()) {
                case 1 -> service.get(msg, out);
                case 2 -> service.add(msg, out);
                case 3 -> service.delete(msg, out);
                case 4 -> service.exit();
                default -> System.out.println("Invalid request received");
            }

        } catch (IOException | ClassNotFoundException exception) {
            System.out.println("Session Error");
        } finally {
            socket.close();
        }
    }

    @Override
    public void run() {
        try {
            handleClientRequest();
        } catch (IOException exception) {
            System.out.println("Session Error: can't clean up socket connection");
            exception.printStackTrace();
        }
    }
}
