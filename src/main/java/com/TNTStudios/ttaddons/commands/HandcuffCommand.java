package com.TNTStudios.ttaddons.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.mojang.brigadier.context.CommandContext;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HandcuffCommand {
    private static final String PERM_HANDCUFF = "ttaddons.esposar";
    private static final Map<UUID, UUID> handcuffedPlayers = new HashMap<>();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("esposar")
                    .requires(source -> hasPermission(source, PERM_HANDCUFF))
                    .then(CommandManager.argument("objetivo", EntityArgumentType.player())
                            .executes(HandcuffCommand::handcuffPlayer)));

            dispatcher.register(CommandManager.literal("quitar_esposas")
                    .requires(source -> hasPermission(source, PERM_HANDCUFF))
                    .executes(HandcuffCommand::releasePlayer));
        });
    }

    private static boolean hasPermission(ServerCommandSource source, String permission) {
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUuid());
            return user != null && user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        }
        return false;
    }

    private static int handcuffPlayer(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity captor = source.getPlayer();

        try {
            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "objetivo");

            if (handcuffedPlayers.containsKey(target.getUuid())) {
                source.sendFeedback(() -> Text.of("¡Este jugador ya está esposado!"), false);
                return 0;
            }

            handcuffedPlayers.put(target.getUuid(), captor.getUuid());
            source.sendFeedback(() -> Text.of("Has esposado a " + target.getName().getString()), false);
            target.sendMessage(Text.of("Has sido esposado por " + captor.getName().getString()));

            // ✅ **Teletransportar usando el comando `/tp`** para compatibilidad con otros mods
            MinecraftServer server = captor.getServer();
            if (server != null) {
                String tpCommand = "tp " + target.getEntityName() + " " + captor.getEntityName();
                server.getCommandManager().getDispatcher().execute(tpCommand, server.getCommandSource());
            }

            // ✅ **Aplicar restricciones al movimiento**
            target.setNoGravity(true);
            target.setVelocity(0, 0, 0);
            target.setSprinting(false);

            return 1;
        } catch (CommandSyntaxException e) {
            source.sendFeedback(() -> Text.of("No se encontró el jugador especificado."), false);
            return 0;
        }
    }

    private static int releasePlayer(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity captor = source.getPlayer();

        for (UUID targetUUID : handcuffedPlayers.keySet()) {
            if (handcuffedPlayers.get(targetUUID).equals(captor.getUuid())) {
                ServerPlayerEntity target = captor.getServer().getPlayerManager().getPlayer(targetUUID);
                if (target != null) {
                    target.setNoGravity(false);
                    target.sendMessage(Text.of("Has sido liberado por " + captor.getName().getString()));
                    source.sendFeedback(() -> Text.of("Has quitado las esposas de " + target.getName().getString()), false);
                }
                handcuffedPlayers.remove(targetUUID);
                return 1;
            }
        }

        source.sendFeedback(() -> Text.of("No tienes a ningún jugador esposado."), false);
        return 0;
    }

    /**
     * ✅ **Método para obtener al captor del jugador esposado**
     * @param targetUUID UUID del jugador esposado.
     * @return UUID del captor si está esposado, de lo contrario null.
     */
    public static UUID getCaptor(UUID targetUUID) {
        return handcuffedPlayers.get(targetUUID);
    }
}
