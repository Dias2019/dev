package org.example.service.server;

import org.example.database.DBManager;
import org.example.model.Request;
import org.example.model.Response;
import org.example.utils.FileManagerUtils;

import java.io.*;

public class FileService {

    private final File dataStorage;
    private final DBManager db;
    private volatile boolean exit = false;


    public FileService(final String dataPath, final String dbPath) {
        db = new DBManager(dbPath);
        dataStorage = new File(dataPath);
    }


    public void get(final Request input, ObjectOutputStream out) throws IOException {
        String filename = input.getFilename();
        if (input.getFilename() == null)
            filename = db.getFilenameById(input.getFileId());
        else if (!db.hasFilename(filename))
            filename = null;

        Response response;
        if (filename == null)
            response = new Response(404);
        else
            response = new Response(200, FileManagerUtils.getFileContent(dataStorage + "/" + filename));

        sendResponse(response, out);
    }

    public void add(final Request input, ObjectOutputStream out) throws IOException {
        if ("".equals(input.getFilename()))
            input.setFilename(FileManagerUtils.generateRandomFilename());

        Response response;
        if (db.hasFilename(input.getFilename()))
            response = new Response(403);
        else {
            FileManagerUtils.saveFile(dataStorage + "/" + input.getFilename(), input.getContent());
            long id = db.insertFilename(input.getFilename());
            response = new Response(200, id);
        }

        sendResponse(response, out);
    }

    public void delete(final Request input, ObjectOutputStream out) throws IOException {
        String filename = input.getFilename();
        if (filename == null)
            filename = db.getFilenameById(input.getFileId());
        else if (!db.hasFilename(filename))
            filename = null;

        Response response;
        if (filename == null)
            response = new Response(404);
        else {
            FileManagerUtils.deleteFile(dataStorage + "/" + filename);
            response = new Response(200);
        }

        sendResponse(response, out);
    }

    public synchronized void exit() {
        exit = true;
        db.serialize();
    }
    public boolean isExit() {
        return exit;
    }


    public Request receiveRequest(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (Request) in.readObject();
    }

    public void sendResponse(final Response response, ObjectOutputStream out) throws IOException {
        out.writeObject(response);
    }
}
