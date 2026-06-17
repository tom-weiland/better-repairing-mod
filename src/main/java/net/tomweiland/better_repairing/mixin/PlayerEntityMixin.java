package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRules;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {
    private static final int DEFAULT_LVL_30_XP = 1395; // Total xp needed to reach level 30 in default Minecraft
    private static final int XP_PER_LVL = Math.ceilDiv(DEFAULT_LVL_30_XP, 30);

    @Shadow
    public int experienceLevel;

    @Inject(method = "getXpNeededForNextLevel", at = @At("HEAD"), cancellable = true)
    private void onGetNextLevelExperience(CallbackInfoReturnable<Integer> cir) {
        // Make xp required to reach the next level constant
        cir.setReturnValue(XP_PER_LVL);
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"))
    private void vanishCursesOnKeepInventory(ServerLevel world, CallbackInfo ci) {
		if (world.getGameRules().get(GameRules.KEEP_INVENTORY)) {
            // Make items with curse of vanishing vanish even when keepInventory is on
            this.destroyVanishingCursedItems();
        }
	}

    @Shadow
    protected abstract void destroyVanishingCursedItems();
}
