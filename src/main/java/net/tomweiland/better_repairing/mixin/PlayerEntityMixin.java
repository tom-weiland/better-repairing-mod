package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    private static final int DEFAULT_LVL_30_XP = 1395; // Total xp needed to reach level 30 in default Minecraft
    private static final int XP_PER_LVL = Math.ceilDiv(DEFAULT_LVL_30_XP, 30);

    @Shadow
    public int experienceLevel;

    @Inject(method = "getNextLevelExperience", at = @At("HEAD"), cancellable = true)
    private void onGetNextLevelExperience(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(XP_PER_LVL); // Make xp required to reach the next level constant
	}
}
