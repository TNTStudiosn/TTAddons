package com.TNTStudios.ttaddons.network;

import com.TNTStudios.ttaddons.config.ConfigManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    private static boolean running = false;

    public static void startServer(MinecraftServer server) {
        if (running) return;
        running = true;

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(ConfigManager.getTcpPort())) {
                System.out.println("Servidor TCP iniciado en el puerto " + ConfigManager.getTcpPort());

                while (running) {
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                        String playerName = in.readLine();
                        if (playerName != null && !playerName.isEmpty()) {
                            if (addPlayerToWhitelist(server, playerName)) {
                                out.println("Jugador " + playerName + " agregado a la whitelist.");
                            } else {
                                out.println("Error al agregar a " + playerName + " a la whitelist.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void stopServer() {
        running = false;
    }

    private static boolean addPlayerToWhitelist(MinecraftServer server, String playerName) {
        try {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player != null) {
                System.out.println("El jugador " + playerName + " ya está en el servidor.");
                return false;
            }

            server.getCommandManager().getDispatcher().execute("whitelist add " + playerName, server.getCommandSource());
            System.out.println("Agregado " + playerName + " a la whitelist.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
