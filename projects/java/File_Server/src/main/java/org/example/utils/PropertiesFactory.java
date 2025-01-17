package org.example.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFactory {

    public static Properties create(final String propertyFile) {
        return PropertiesFactory.readPropertiesFile(propertyFile);
    }

    private static Properties readPropertiesFile(final String path) {
        Properties prop = null;
        try (FileInputStream fis = new FileInputStream(path)) {
            prop = new Properties();
            prop.load(fis);
        } catch(IOException e) {
            System.out.println("Error with reading properties file: " + e.getMessage());
        }
        return prop;
    }
}
