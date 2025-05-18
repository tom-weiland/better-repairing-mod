package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Redirect(
        method = "forEachEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/enchantment/EnchantmentHelper$ContextAwareConsumer;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private static boolean isEmptyOrFullyDamaged(ItemStack stack) {
        // Make it not apply enchantments if the item durability is 0
        return stack.isEmpty() || stack.isFullyDamaged();
    }
}
