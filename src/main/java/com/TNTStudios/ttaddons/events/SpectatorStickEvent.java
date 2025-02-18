package com.TNTStudios.ttaddons.events;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class SpectatorStickEvent {

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                ItemStack heldItem = serverPlayer.getMainHandStack();

                if (heldItem.getItem() == Items.STICK && heldItem.hasEnchantments() && entity instanceof ServerPlayerEntity target) {
                    target.changeGameMode(net.minecraft.world.GameMode.SPECTATOR);
                    serverPlayer.sendMessage(net.minecraft.text.Text.of("Has convertido a " + target.getEntityName() + " en espectador."));
                    target.sendMessage(net.minecraft.text.Text.of("¡Has sido convertido en espectador por " + serverPlayer.getEntityName() + "!"));
                }
            }
            return ActionResult.PASS;
        });
    }
}
