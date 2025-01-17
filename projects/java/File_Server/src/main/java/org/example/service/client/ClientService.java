package org.example.service.client;

import org.example.model.Request;
import org.example.model.Response;
import org.example.utils.FileManagerUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class ClientService {

    private final Scanner scanner = new Scanner(System.in);
    private final File clientDataFolder;
    private final String ADDRESS;
    private final String PORT;

    public ClientService(final Properties clientProperties) {
        ADDRESS = clientProperties.getProperty("address");
        PORT = clientProperties.getProperty("port");
        clientDataFolder = new File(clientProperties.getProperty("client_storage"));
    }


    public final void start() {

        try (
            Socket socket = new Socket(InetAddress.getByName(ADDRESS), Integer.parseInt(PORT));
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ){
            System.out.print("Enter action (1 - get a file, 2 - save a file, 3 - delete a file): ");
            String command = scanner.nextLine();
            if ("exit".matches(command)) {
                exit(out);
                return;
            }

            switch (Integer.parseInt(command)) {
                case 1 -> get(in, out);
                case 2 -> add(in, out);
                case 3 -> delete(in, out);
                default -> System.out.println("Invalid request!");
            }
        } catch (NumberFormatException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private void get(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
        int byNameOrId = scanner.nextInt(); scanner.nextLine();

        String filename = null; int fileId = -1;
        if (byNameOrId == 1) {
            System.out.print("Enter name of the file: ");
            filename = scanner.nextLine();
        } else if (byNameOrId == 2) {
            System.out.print("Enter id: ");
            fileId = scanner.nextInt(); scanner.nextLine();
        }

        sendRequest(new Request(1, filename, fileId), out);

        Response response = receiveResponse(in);
        if (response.getStatus() == 200) {
            System.out.print("The file was downloaded! Specify a name for it: ");
            String clientFilename = scanner.nextLine();
            FileManagerUtils.saveFile(clientDataFolder + "/" + clientFilename, response.getContent());
            System.out.println("File saved on the hard drive!");
        }
        else if (response.getStatus() == 404)
            System.out.println("The response says that this file is not found!");
        else
            System.out.println("Unknown exception status during GET request");
    }


    private void add(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        System.out.print("Enter name of the file: ");
        String clientFilename = scanner.nextLine();
        System.out.print("Enter name of the file to be saved on server: ");
        String serverFilename = scanner.nextLine();
        byte[] content = FileManagerUtils.getFileContent(clientDataFolder + "/" + clientFilename);

        sendRequest(new Request(2, serverFilename, content), out);

        Response response = receiveResponse(in);
        if (response.getStatus() == 200)
            System.out.printf("Response says that file is saved! ID = %d", response.getFileId());
        else if (response.getStatus() == 403)
            System.out.println("The response says that creating the file was forbidden!");
        else
            System.out.println("Unknown exception status during PUT request");
    }


    private void delete(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
        int byNameOrId = scanner.nextInt(); scanner.nextLine();

        String filename = null; int fileId = -1;
        if (byNameOrId == 1) {
            System.out.print("Enter name of the file: ");
            filename = scanner.nextLine();
        } else if (byNameOrId == 2) {
            System.out.print("Enter id: ");
            fileId = scanner.nextInt(); scanner.nextLine();
        }

        sendRequest(new Request(3, filename, fileId), out);

        Response response = receiveResponse(in);
        if (response.getStatus() == 200)
            System.out.println("The response says that this file was deleted successfully!");
        else if (response.getStatus() == 404)
            System.out.println("The response says that this file is not found!");
        else
            System.out.println("Unknown exception status during DELETE request");
    }

    private void exit(ObjectOutputStream out) throws IOException {
        sendRequest(new Request(4), out);
    }


    private void sendRequest(Request msg, ObjectOutputStream out) throws IOException {
        out.writeObject(msg);
        System.out.println("The request was sent.");
    }

    private Response receiveResponse(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (Response) in.readObject();
    }

}
