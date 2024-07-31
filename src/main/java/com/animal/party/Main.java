package com.animal.party;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Utils {
    public static Configuration config;

    public static void main(String[] args) throws InterruptedException {
        String configPath;
        if (isRunningFromJar()) {
            configPath = "config.yml";
        } else {
            configPath = "src/main/resources/config.yml";
        }

        File file = new File(configPath);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            config = objectMapper.readValue(file, Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        App.appInitialize();
    }

    public static class Configuration {
        public Configuration() {}

        private AppConfig app;

        public AppConfig getApp() {
            return app;
        }

        public static class AppConfig {
            public String prefix;
            public String TOKEN;
            public boolean global;
        }

    }
    private static boolean isRunningFromJar() {
        URL resource = Main.class.getResource(Main.class.getSimpleName() + ".class");
        return resource != null && resource.getProtocol().equals("jar");
    }
}