package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    private static final int DEFAULT_LVL_30_XP = 1395; // Total xp needed to reach level 30 in default Minecraft
    private static final int XP_PER_LVL = Math.ceilDiv(DEFAULT_LVL_30_XP, 30);

    @Shadow
    public int experienceLevel;

    @Inject(method = "getNextLevelExperience", at = @At("HEAD"), cancellable = true)
    private void onGetNextLevelExperience(CallbackInfoReturnable<Integer> cir) {
        // Make xp required to reach the next level constant
        cir.setReturnValue(XP_PER_LVL);
    }

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void vanishCursesOnKeepInventory(CallbackInfo ci, @Local(ordinal = 0) ServerWorld world) {
		if (world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            // Make items with curse of vanishing vanish even when keepInventory is on
            this.vanishCursedItems();
        }
	}

    @Shadow
    protected abstract void vanishCursedItems();
}
