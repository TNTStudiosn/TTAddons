package com.TNTStudios.ttaddons;

import com.TNTStudios.ttaddons.commands.HandcuffCommand;
import com.TNTStudios.ttaddons.commands.HelpCommand;
import com.TNTStudios.ttaddons.commands.ThorCommand;
import com.TNTStudios.ttaddons.config.ConfigManager;
import com.TNTStudios.ttaddons.events.ServerEventListener;
import com.TNTStudios.ttaddons.network.TcpServer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;


public class Ttaddons implements ModInitializer {

    private static MinecraftServer serverInstance;

    @Override
    public void onInitialize() {
        HandcuffCommand.register();
        ThorCommand.register();
        HelpCommand.register();
        ConfigManager.loadConfig();
        ServerEventListener.register(); // Se registra antes de iniciar el servidor TCP

        // Iniciamos el servidor TCP solo si ya se ha inicializado el servidor de Minecraft
        if (serverInstance != null) {
            TcpServer.startServer(serverInstance);
        }
    }

    public static void setServerInstance(MinecraftServer server) {
        serverInstance = server;
    }
}
