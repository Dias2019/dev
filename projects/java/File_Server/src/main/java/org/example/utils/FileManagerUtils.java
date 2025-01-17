package org.example.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileManagerUtils {

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public static byte[] getFileContent(String file) {
        Path path = Paths.get(file);
        byte[] content = null;

        lock.readLock().lock();
        try {
            content = Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println("Error with reading file: " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }

        return content;
    }

    public static void saveFile(final String file, byte[] content) {
        Path path = Paths.get(file);

        lock.writeLock().lock();
        try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
            fos.write(content, 0, content.length);
        } catch (Exception e) {
            System.out.println("Error with saving file: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void deleteFile(final String file) {
        Path path = Paths.get(file);

        lock.writeLock().lock();
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.err.println("Error with deleting file: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static String generateRandomFilename() {
        return UUID.randomUUID().toString();
    }
}
