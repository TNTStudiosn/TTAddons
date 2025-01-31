package com.TNTStudios.ttaddons.mixin;

import com.TNTStudios.ttaddons.commands.HandcuffCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class HandcuffMovementMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void preventMovementAndFollowCaptor(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        UUID captorUUID = HandcuffCommand.getCaptor(player.getUuid());

        if (captorUUID != null) {
            ServerPlayerEntity captor = player.getServer().getPlayerManager().getPlayer(captorUUID);
            if (captor != null) {
                // ✅ Bloquear el movimiento manual pero mantener la cámara libre
                player.setVelocity(Vec3d.ZERO);
                player.velocityModified = true;

                // ✅ Obtener la posición del captor sin tomar en cuenta su mirada vertical
                Vec3d captorPos = captor.getPos();
                Vec3d captorLookDir = captor.getRotationVector();

                // ✅ Mantener la altura real del captor sin acumulaciones incorrectas
                double targetY = captor.getY();

                // ✅ Si el captor está en el aire, usar su velocidad Y para hacer que el esposado salte de forma natural
                if (!captor.isOnGround()) {
                    targetY = Math.max(targetY + captor.getVelocity().y, player.getY()); // Evita acumulaciones
                }

                // Solo tomar en cuenta los ejes X y Z para la posición detrás del captor
                Vec3d behindCaptor = new Vec3d(
                        captorPos.x - captorLookDir.x * 1,  // 0.7 bloques detrás en X
                        targetY,                              // Mantener altura del captor correctamente
                        captorPos.z - captorLookDir.z * 1  // 0.7 bloques detrás en Z
                );

                // ✅ Interpolación suave para evitar movimientos bruscos
                Vec3d smoothMovement = behindCaptor.subtract(player.getPos()).multiply(0.3);

                // ✅ Aplicar el movimiento suave con la altura corregida
                player.setVelocity(smoothMovement.x, smoothMovement.y, smoothMovement.z);
                player.velocityModified = true;

                // ✅ Cancelar la posibilidad de movimiento manual
                ci.cancel();
            }
        }
    }
}
