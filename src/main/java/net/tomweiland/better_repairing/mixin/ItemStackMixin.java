package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;

import net.tomweiland.better_repairing.BetterRepairing;
import net.tomweiland.better_repairing.ItemStackExtensions;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder, FabricItemStack, ItemStackExtensions {
    
    @Override
    public boolean isFullyDamaged() {
        return this.isDamageable() && this.getDamage() == this.getMaxDamage();
    }

    @Override
    public boolean hasEnchantment(RegistryKey<Enchantment> enchantment) {
        var enchants = EnchantmentHelper.getEnchantments((ItemStack)(Object)this).getEnchantments();
        for (var enchant : enchants) {
            if (enchant.matchesKey(enchantment)) {
                return true;
            }
        }
		return false;
	}

    @Redirect(method = "onDurabilityChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shouldBreak()Z"))
    private boolean durabilityChangeShouldBreak(ItemStack stack) {
        if (BetterRepairing.isMendingArmor(stack)) {
            // Allow mending armor to exist at durability level 0 without breaking
            return stack.isDamageable() && stack.getDamage() > stack.getMaxDamage();
        }
        return stack.shouldBreak();
    }

    @Shadow
    public abstract boolean isDamageable();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract ItemEnchantmentsComponent getEnchantments();
}