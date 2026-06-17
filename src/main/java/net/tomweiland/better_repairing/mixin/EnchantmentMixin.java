package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin{

    @Inject(method = "areCompatible", at = @At("RETURN"), cancellable = true)
    private static void onCanBeCombined(Holder<Enchantment> first, Holder<Enchantment> second, CallbackInfoReturnable<Boolean> cir) {
        if ((first.is(Enchantments.BINDING_CURSE) && second.is(Enchantments.MENDING)) || (first.is(Enchantments.MENDING) && second.is(Enchantments.BINDING_CURSE))) {
            // Prevent curse of binding from being combined with mending
            cir.setReturnValue(false);
        }
	}
}
