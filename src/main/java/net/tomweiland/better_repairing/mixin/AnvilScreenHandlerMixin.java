package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import net.tomweiland.better_repairing.BetterRepairing;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {

	private static final int baseEnchantCost = 5;
	private static final int renameCost = 1;

    @Shadow
    private DataSlot cost;
    @Shadow
    private boolean onlyRenaming;
    @Shadow
    private int repairItemCountCost;
    @Shadow
    private String itemName;

    private AnvilScreenHandlerMixin(int syncId, Inventory inventory, ContainerLevelAccess context) {
		super(MenuType.ANVIL, syncId, inventory, context, null);
		this.addDataSlot(this.cost);
	}

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void onUpdateResult(CallbackInfo ci) {
		// Based on Minecraft's AnvilMenu.createResult method. This could probably be done more surgically but since we're making
		// substantial changes to anvil behaviour, a brute-force replacement of the original logic like this is probably fiiine...
        ItemStack itemInput1 = this.inputSlots.getItem(0); // Formerly: itemStack1
		this.onlyRenaming = false;
		this.cost.set(1);
		int cost = 0; // Formerly: i
		boolean isRenaming = false;
		boolean isModifying = false;
		int newEnchantCost = 0;

		if (!itemInput1.isEmpty() && EnchantmentHelper.canStoreEnchantments(itemInput1)) {
			ItemStack itemOutput = itemInput1.copy(); // Formerly: itemStack2
			ItemStack itemInput2 = this.inputSlots.getItem(1); // Formerly: itemStack3
			ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(itemOutput));
			boolean item1HasMending = itemOutput.hasEnchantment(Enchantments.MENDING);
			this.repairItemCountCost = 0;

			if (!itemInput2.isEmpty()) {
				boolean item2IsEnchantedBook = itemInput2.has(DataComponents.STORED_ENCHANTMENTS); // Formerly: bl

				boolean item1IsNetherite = itemInput1.isValidRepairItem(new ItemStack(Items.NETHERITE_INGOT));
				boolean item2IsDiamond = itemInput2.is(Items.DIAMOND);
				boolean isRepairingNetheriteWithDiamond = item1IsNetherite && item1HasMending && item2IsDiamond; // Allow netherite items with mending to be repaired with diamonds

				if (itemOutput.isDamageableItem() && (itemInput1.isValidRepairItem(itemInput2) || isRepairingNetheriteWithDiamond)) {
					int numRepairsForMaxDmg = 3;
					boolean item2IsNetherite = itemInput2.is(Items.NETHERITE_INGOT);
					if (item2IsNetherite) {
						numRepairsForMaxDmg = 1; // Only require 1 netherite ingot to fully repair a netherite tool
					} else if (item1HasMending) {
						numRepairsForMaxDmg = 2;
					}
					int maxDmgRepair = itemOutput.getMaxDamage() / numRepairsForMaxDmg + 1;
					int dmgRepair = Math.min(itemOutput.getDamageValue(), maxDmgRepair); // Formerly: k
					if (dmgRepair <= 0) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						ci.cancel();
						return;
					}

					int repairItemsUsed; // Formerly: m
					int unitRepairCost = 0;
					if (isRepairingNetheriteWithDiamond) {
						unitRepairCost += 1; // Charge more levels for repairing netherite gear with diamonds
					}
					for (repairItemsUsed = 0; dmgRepair > 0 && repairItemsUsed < itemInput2.getCount(); repairItemsUsed++) {
						int n = itemOutput.getDamageValue() - dmgRepair;
						itemOutput.setDamageValue(n);
						cost += unitRepairCost;
						dmgRepair = Math.min(itemOutput.getDamageValue(), maxDmgRepair);
						isModifying = true;
					}

					this.repairItemCountCost = repairItemsUsed;
				} else {
					if (!item2IsEnchantedBook && (!itemOutput.is(itemInput2.getItem()) || !itemOutput.isDamageableItem())) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						ci.cancel();
						return;
					}

					if (itemOutput.isDamageableItem() && !item2IsEnchantedBook) {
						int item1RemainDur = itemInput1.getMaxDamage() - itemInput1.getDamageValue(); // Formerly: kx
						int item2RemainDur = itemInput2.getMaxDamage() - itemInput2.getDamageValue(); // Formerly: m
						int n = item2RemainDur + itemOutput.getMaxDamage() * 12 / 100;
						int itemOutputDur = item1RemainDur + n; // Formerly: o
						int itemOutputDmg = itemOutput.getMaxDamage() - itemOutputDur; // Formerly: p
						if (itemOutputDmg < 0) {
							itemOutputDmg = 0;
						}

						if (itemOutputDmg < itemOutput.getDamageValue()) {
							itemOutput.setDamageValue(itemOutputDmg);
							cost += 5;
							isModifying = true;
						}
					}

					ItemEnchantments itemEnchantmentsComponent = EnchantmentHelper.getEnchantmentsForCrafting(itemInput2);
					boolean hasAcceptableEnchant = false; // Formerly: bl2
					boolean hasUnacceptableEnchant = false; // Formerly: bl3

					for (Entry<Holder<Enchantment>> entry : itemEnchantmentsComponent.entrySet()) {
						Holder<Enchantment> item2Enchant = entry.getKey(); // Formerly: registryEntry
						int item1EnchantLvl = builder.getLevel(item2Enchant); // Formerly: q
						int item2EnchantLvl = entry.getIntValue(); // Formerly: r
						item2EnchantLvl = item1EnchantLvl == item2EnchantLvl ? item2EnchantLvl + 1 : Math.max(item2EnchantLvl, item1EnchantLvl);
						Enchantment enchantment = item2Enchant.value();
						boolean isAcceptableEnchant = enchantment.canEnchant(itemInput1); // Formerly: bl4
						if (this.player.hasInfiniteMaterials() || itemInput1.is(Items.ENCHANTED_BOOK)) {
							isAcceptableEnchant = true;
						}

						for (Holder<Enchantment> item1Enchant : builder.keySet()) {
							if (!item1Enchant.equals(item2Enchant) && !Enchantment.areCompatible(item2Enchant, item1Enchant)) {
								isAcceptableEnchant = false;
								if (item2Enchant.is(Enchantments.BINDING_CURSE)) {
									// If binding curse can't be added, don't allow the items to be combined (as that would make it possible to remove curses)
									this.resultSlots.setItem(0, ItemStack.EMPTY);
									this.cost.set(0);
									ci.cancel();
									return;
								}
							}
						}

						if (!isAcceptableEnchant) {
							hasUnacceptableEnchant = true;
							newEnchantCost += 2; // Incompatible enchants penalty
						} else {
							hasAcceptableEnchant = true;
							if (item2EnchantLvl > enchantment.getMaxLevel()) {
								item2EnchantLvl = enchantment.getMaxLevel();
							}

							builder.set(item2Enchant, item2EnchantLvl);
							newEnchantCost++;
						}
					}

					if (hasUnacceptableEnchant && !hasAcceptableEnchant) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						ci.cancel();
						return;
					}
				}
			}

			if (newEnchantCost > 0) {
				cost += baseEnchantCost;
				isModifying = true;
			}

			if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
				if (!this.itemName.equals(itemInput1.getHoverName().getString())) {
					isRenaming = true;
					cost += renameCost;
					itemOutput.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
				}
			} else if (itemInput1.has(DataComponents.CUSTOM_NAME)) {
				isRenaming = true;
				cost += renameCost;
				itemOutput.remove(DataComponents.CUSTOM_NAME);
			}

			if (isModifying) {
				// Enchant level tax only applies when modifying the item OTHER than renaming
				EnchantmentHelper.setEnchantments(itemOutput, builder.toImmutable());
				int enchantLvlTax = EnchantmentHelper.getEnchantmentsForCrafting(itemOutput).size();
				if (this.repairItemCountCost > 0) {
					enchantLvlTax *= this.repairItemCountCost;
				}

				if (itemOutput.hasEnchantment(Enchantments.BINDING_CURSE) || (!BetterRepairing.isHardcore(this.player.level()) && itemOutput.hasEnchantment(Enchantments.VANISHING_CURSE))) {
					// Give curses an upside/positive effect so that there's a tradeoff and an interesting decision to be made
					// Only applies to curse of vanishing when not in hardcore mode
					enchantLvlTax = 0;
				}

				cost += enchantLvlTax;
			}

			cost = Math.max(cost, 0);
			this.resultSlots.setItem(0, (isRenaming || isModifying) ? itemOutput : ItemStack.EMPTY);
			this.cost.set(cost);
			this.broadcastChanges();
		} else {
			this.resultSlots.setItem(0, ItemStack.EMPTY);
			this.cost.set(0);
		}

        ci.cancel();
    }
}
