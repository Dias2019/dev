package org.example;

import org.example.service.server.ServerService;
import org.example.utils.PropertiesFactory;

import java.util.Properties;

public class ServerMain {
    public static void main(String[] args) {
        final Properties serviceProperties = PropertiesFactory.create("config/dev.properties");
        final ServerService server = new ServerService(serviceProperties);
        server.start();
    }
}
