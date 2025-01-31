package com.TNTStudios.ttaddons.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class HelpCommand {

    private static final String PERM_HELP_RECEIVE = "ttaddons.ayuda";

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("ayuda")
                    .then(CommandManager.argument("mensaje", MessageArgumentType.message())
                            .executes(context -> {
                                String message = MessageArgumentType.getMessage(context, "mensaje").getString();
                                return sendHelpMessage(context.getSource(), message);
                            })));
        });
    }

    private static int sendHelpMessage(ServerCommandSource source, String message) {
        if (!(source.getEntity() instanceof ServerPlayerEntity sender)) {
            source.sendFeedback(() -> Text.of("Este comando solo puede ser usado por jugadores."), false);
            return 0;
        }

        // Crear el mensaje con formato llamativo
        Text formattedMessage = Text.literal("⚠ ¡Solicitud de Ayuda! ⚠\n")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFAA00)).withBold(true))
                .append(Text.literal(sender.getName().getString() + " necesita ayuda: ")
                        .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)))
                .append(Text.literal(message)
                        .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true)));

        // Enviar mensaje a todos los jugadores con permiso
        int recipients = 0;
        for (ServerPlayerEntity player : sender.getServer().getPlayerManager().getPlayerList()) {
            if (hasPermission(player, PERM_HELP_RECEIVE)) {
                player.sendMessage(formattedMessage, false);
                recipients++;
            }
        }

        // Confirmación al solicitante
        sender.sendMessage(Text.literal("✔ Tu mensaje de ayuda ha sido enviado.")
                .setStyle(Style.EMPTY.withColor(Formatting.GREEN)), false);

        return recipients > 0 ? 1 : 0;
    }

    private static boolean hasPermission(ServerPlayerEntity player, String permission) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUuid());
        return user != null && user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
}
