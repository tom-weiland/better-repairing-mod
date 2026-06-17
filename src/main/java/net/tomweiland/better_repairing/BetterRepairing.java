package net.tomweiland.better_repairing;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterRepairing implements ModInitializer {
	public static final String MOD_ID = "better_repairing";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		
	}

	public static Identifier getId(String name) {
		return Identifier.fromNamespaceAndPath(MOD_ID, name);
	}

	public static boolean isMendingArmor(ItemStack stack) {
		boolean isArmor = stack.is(ItemTags.HEAD_ARMOR) || stack.is(ItemTags.CHEST_ARMOR) || stack.is(ItemTags.LEG_ARMOR) || stack.is(ItemTags.FOOT_ARMOR);
		return isArmor && stack.hasEnchantment(Enchantments.MENDING);
	}

	public static boolean isHardcore(Level world) {
		return world.getLevelData().isHardcore();
	}
}