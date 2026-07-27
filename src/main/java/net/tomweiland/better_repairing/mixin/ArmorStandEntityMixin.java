package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;

@Mixin(ArmorStand.class)
public abstract class ArmorStandEntityMixin {
    @Redirect(
        method = "brokenByAnything",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;has(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/component/DataComponentType;)Z")
    )
    private boolean shouldPreventEquipmentDrop(ItemStack stack, DataComponentType<?> component) {
        // Make curse of vanishing equipment drop normally when the armor stand is broken
        return false;
    }
}
