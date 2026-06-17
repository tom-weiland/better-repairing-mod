package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbEntityMixin extends Entity {

    public ExperienceOrbEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "repairPlayerItems", at = @At("HEAD"), cancellable = true)
    private void onRepairPlayerGears(ServerPlayer player, int amount, CallbackInfoReturnable<Integer> cir) {
        // Prevent xp from being used to repair mending gear
        cir.setReturnValue(amount);
    }
}
