package net.tomweiland.better_repairing;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;

public interface ItemStackExtensions {
    default boolean isFullyDamaged() {
        return false;
    }

    default boolean hasEnchantment(RegistryKey<Enchantment> enchantment) {
		return false;
	}
}
