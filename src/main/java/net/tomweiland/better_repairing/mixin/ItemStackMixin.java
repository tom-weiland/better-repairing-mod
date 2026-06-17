package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import net.tomweiland.better_repairing.BetterRepairing;
import net.tomweiland.better_repairing.ItemStackExtensions;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder, FabricItemStack, ItemStackExtensions {

    @Override
    public boolean isFullyDamaged() {
        return this.isDamageableItem() && this.getDamageValue() == this.getMaxDamage();
    }

    @Override
    public boolean hasEnchantment(ResourceKey<Enchantment> enchantment) {
        var enchants = EnchantmentHelper.getEnchantmentsForCrafting((ItemStack)(Object)this).keySet();
        for (var enchant : enchants) {
            if (enchant.is(enchantment)) {
                return true;
            }
        }
		return false;
	}

    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isBroken()Z"))
    private boolean durabilityChangeShouldBreak(ItemStack stack) {
        if (BetterRepairing.isMendingArmor(stack)) {
            // Allow mending armor to exist at durability level 0 without breaking
            return stack.isDamageableItem() && stack.getDamageValue() > stack.getMaxDamage();
        }
        return stack.isBroken();
    }

    @Shadow
    public abstract boolean isDamageableItem();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract int getDamageValue();

    @Shadow
    public abstract ItemEnchantments getEnchantments();
}
