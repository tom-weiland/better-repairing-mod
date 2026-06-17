package net.tomweiland.better_repairing;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.resources.ResourceKey;

public interface ItemStackExtensions {
    default boolean isFullyDamaged() {
        return false;
    }

    default boolean hasEnchantment(ResourceKey<Enchantment> enchantment) {
		return false;
	}
}
