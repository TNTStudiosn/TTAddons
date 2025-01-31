package com.TNTStudios.ttaddons.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class ThorCommand {

    private static final String PERM_THOR = "ttaddons.thor";

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("thor")
                    .requires(source -> hasPermission(source, PERM_THOR))
                    .executes(context -> summonLightningAtLook(context.getSource()))
                    .then(CommandManager.argument("jugador", EntityArgumentType.player())
                            .executes(context -> summonLightningOnPlayer(context.getSource(), EntityArgumentType.getPlayer(context, "jugador")))));
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

    private static int summonLightningAtLook(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        World world = player.getWorld();

        // Obtener la posición del bloque que está mirando el jugador
        HitResult hitResult = player.raycast(100, 0, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos targetPos = blockHit.getBlockPos();

            LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
            if (lightning != null) {
                lightning.refreshPositionAfterTeleport(targetPos.getX(), targetPos.getY(), targetPos.getZ());
                world.spawnEntity(lightning);
            }

            source.sendFeedback(() -> Text.of("⚡ Invocaste un rayo en " + targetPos.getX() + ", " + targetPos.getY() + ", " + targetPos.getZ()), false);
            return 1;
        } else {
            source.sendFeedback(() -> Text.of("⚠ No estás mirando un bloque válido."), false);
            return 0;
        }
    }

    private static int summonLightningOnPlayer(ServerCommandSource source, ServerPlayerEntity target) {
        World world = target.getWorld();
        Vec3d targetPos = target.getPos();

        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.refreshPositionAfterTeleport(targetPos.getX(), targetPos.getY(), targetPos.getZ());
            world.spawnEntity(lightning);
        }

        source.sendFeedback(() -> Text.of("⚡ Has invocado un rayo sobre " + target.getName().getString()), false);
        target.sendMessage(Text.of("⚠ Un rayo ha caído sobre ti!"), false);

        return 1;
    }
}
