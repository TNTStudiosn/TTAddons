package com.TNTStudios.ttaddons.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SpectatorStickCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("spectatorstick")
                    .requires(source -> source.hasPermissionLevel(4)) // Solo OP nivel 4
                    .executes(context -> giveSpectatorStick(context.getSource().getPlayer())));
        });
    }

    private static int giveSpectatorStick(ServerPlayerEntity player) {
        if (player == null) return 0;

        ItemStack stick = new ItemStack(Items.STICK);
        NbtCompound tag = new NbtCompound();
        tag.putString("CustomName", "{\"text\":\"Bastón Fantasmal\",\"italic\":false,\"color\":\"gold\"}");
        tag.putBoolean("Unbreakable", true);
        stick.setNbt(tag);
        stick.addEnchantment(net.minecraft.enchantment.Enchantments.KNOCKBACK, 1);

        player.getInventory().insertStack(stick);
        player.sendMessage(Text.literal("Has recibido el Bastón Fantasmal.").formatted(Formatting.GOLD), false);

        return 1;
    }
}
