package com.TNTStudios.ttaddons.events;

import com.TNTStudios.ttaddons.Ttaddons;
import com.TNTStudios.ttaddons.network.TcpServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class ServerEventListener {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
            Ttaddons.setServerInstance(server);
            TcpServer.startServer(server); // Ahora se inicia aquí, cuando el servidor está disponible
        });
    }
}
