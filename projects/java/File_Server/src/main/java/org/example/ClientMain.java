package org.example;

import org.example.service.client.ClientService;
import org.example.utils.PropertiesFactory;

import java.util.Properties;

public class ClientMain {
    public static void main(String[] args) {
        final Properties serviceProperties = PropertiesFactory.create("config/dev.properties");
        final ClientService client = new ClientService(serviceProperties);
        client.start();
    }
}
