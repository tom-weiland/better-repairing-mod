package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.entry.RegistryEntry;

@Mixin(Enchantment.class)
public class EnchantmentMixin{

    @Inject(method = "canBeCombined", at = @At("RETURN"), cancellable = true)
    private static void onCanBeCombined(RegistryEntry<Enchantment> first, RegistryEntry<Enchantment> second, CallbackInfoReturnable<Boolean> cir) {
        if ((first.matchesKey(Enchantments.BINDING_CURSE) && second.matchesKey(Enchantments.MENDING)) || (first.matchesKey(Enchantments.MENDING) && second.matchesKey(Enchantments.BINDING_CURSE))) {
            // Prevent curse of binding from being combined with mending
            cir.setReturnValue(false);
        }
	}
}
