package com.hotkeyhelper.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hotkeyhelper.model.AppConfig;
import com.hotkeyhelper.model.ShortcutsConfig;
import com.hotkeyhelper.model.WindowInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URISyntaxException;

public class ShortcutRepository {

    private static final String DATA_DIR = "data";
    private static final String SHORTCUTS_DIR = "shortcuts";
    private static final String CONFIG_FILE = "config.json";

    private final Gson gson;
    private ShortcutsConfig config;
    private final Path dataPath;
    private final Path shortcutsPath;
    private final Path configPath;

    public ShortcutRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataPath = resolveAppDir().resolve(DATA_DIR);
        this.shortcutsPath = dataPath.resolve(SHORTCUTS_DIR);
        this.configPath = dataPath.resolve(CONFIG_FILE);

        initializeDirectories();
        loadConfig();
    }

    private static Path resolveAppDir() {
        // 1. Системное свойство jpackage — путь к папке с .exe
        String appDir = System.getProperty("jpackage.app-path");
        if (appDir != null) {
            return Path.of(appDir).getParent();
        }

        // 2. Fallback: расположение JAR/class-файлов
        try {
            Path jarPath = Path.of(ShortcutRepository.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            // При jpackage: app/module.jar -> поднимаемся на 2 уровня
            // При gradlew run: build/classes/java/main -> поднимаемся до корня проекта
            Path parent = jarPath;
            while (parent != null) {
                if (Files.exists(parent.resolve("data"))) {
                    return parent;
                }
                parent = parent.getParent();
            }
        } catch (Exception ignored) {
        }

        // 3. Последний fallback: рабочая директория
        return Paths.get(".");
    }

    private void initializeDirectories() {
        try {
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
            if (!Files.exists(shortcutsPath)) {
                Files.createDirectories(shortcutsPath);
            }
            if (!Files.exists(configPath)) {
                createDefaultConfig();
            }
        } catch (IOException e) {
            System.err.println("Error initializing directories: " + e.getMessage());
        }
    }

    private void createDefaultConfig() {
        try {
            ShortcutsConfig defaultConfig = new ShortcutsConfig();
            defaultConfig.setApps(java.util.Collections.emptyList());
            String json = gson.toJson(defaultConfig);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            System.err.println("Error creating default config: " + e.getMessage());
        }
    }

    private void loadConfig() {
        try {
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);
                config = gson.fromJson(json, ShortcutsConfig.class);
                if (config == null || config.getApps() == null) {
                    config = new ShortcutsConfig();
                    config.setApps(java.util.Collections.emptyList());
                }
            } else {
                config = new ShortcutsConfig();
                config.setApps(java.util.Collections.emptyList());
            }
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
            config = new ShortcutsConfig();
            config.setApps(java.util.Collections.emptyList());
        }
    }

    public void reload() {
        loadConfig();
    }

    public String getShortcutsHtml(WindowInfo windowInfo) {
        if (windowInfo == null || config == null || config.getApps() == null) {
            return null;
        }

        for (AppConfig appConfig : config.getApps()) {
            if (appConfig.matches(windowInfo)) {
                return loadHtmlFile(appConfig.getHtmlFile());
            }
        }

        return null;
    }

    private String loadHtmlFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        Path htmlPath = shortcutsPath.resolve(fileName);

        try {
            if (Files.exists(htmlPath)) {
                return Files.readString(htmlPath);
            }
        } catch (IOException e) {
            System.err.println("Error loading HTML file " + fileName + ": " + e.getMessage());
        }

        return null;
    }
}
