package org.example.database;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class DBManager {

    private static final AtomicLong counter = new AtomicLong();
    private final Map<Long, String> idToFilename;
    private final File dbFile;

    public DBManager(final String dbPath) {
        dbFile = new File(dbPath);
        idToFilename = new ConcurrentHashMap<>();
        deserialize();
    }


    public String getFilenameById(long id) {
        return idToFilename.get(id);
    }

    public boolean hasFilename(String name) {
        return idToFilename.values().stream().anyMatch(name::equals);
    }

    public synchronized long insertFilename(String name) {
        long id = counter.incrementAndGet();
        idToFilename.put(id, name);
        return id;
    }


    public void serialize() {

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(dbFile))) {
            for (Map.Entry<Long, String> entry : idToFilename.entrySet()) {
                bf.write(entry.getKey() + ":" + entry.getValue());
                bf.newLine();
            }
            bf.flush();
        } catch (IOException exception) {
            System.out.println("Error when serializing filenameToId map");
            exception.printStackTrace();
        }
    }

    private void deserialize() {

        long maxId = -1;
        try (BufferedReader  bf = new BufferedReader(new FileReader(dbFile))) {
            String line;
            while ((line = bf.readLine()) != null) {

                String[] parts = line.split(":");

                Long id = (parts[0].trim().equals("")) ? null : Long.parseLong(parts[0].trim());
                String filename = parts[1].trim();

                if (!filename.equals("") && id != null) {
                    idToFilename.put(id, filename);
                    maxId = Math.max(maxId, id);
                }
            }
        } catch (IOException exception) {
            System.out.println("Error when deserializing filenameToId map");
            exception.printStackTrace();
        }

        if (maxId != -1) counter.set(maxId);
    }
}
