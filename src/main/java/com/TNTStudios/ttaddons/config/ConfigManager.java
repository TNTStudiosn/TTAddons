package com.TNTStudios.ttaddons.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "ttaddons_config.json");
    private static int tcpPort = 25566; // Valor predeterminado

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            saveConfig(); // Crear el archivo con valores por defecto si no existe
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            if (json.has("tcp_port")) {
                tcpPort = json.get("tcp_port").getAsInt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        JsonObject json = new JsonObject();
        json.addProperty("tcp_port", tcpPort);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            new Gson().toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getTcpPort() {
        return tcpPort;
    }
}
