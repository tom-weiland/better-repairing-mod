package net.tomweiland.better_repairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;

import net.tomweiland.better_repairing.BetterRepairing;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

	private static final int baseRepairCost = 2;
	private static final int baseEnchantCost = 3;

    @Shadow
    private Property levelCost;
    @Shadow
    private boolean keepSecondSlot;
    @Shadow
    private int repairItemUsage;
    @Shadow
    private String newItemName;

    private AnvilScreenHandlerMixin(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.ANVIL, syncId, inventory, context, null);
		this.addProperty(this.levelCost);
	}

    @Shadow
    private static int getNextCost(int cost) { return 0; }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void onUpdateResult(CallbackInfo ci) {
		// Based on Minecraft's AnvilScreenHandler.updateResult method. This could probably be done more surgically but since we're making
		// substantial changes to anvil behaviour, a brute-force replacement of the original logic like this is probably fiiine...
        ItemStack itemInput1 = this.input.getStack(0); // Formerly: itemStack1
		this.keepSecondSlot = false;
		this.levelCost.set(1);
		int cost = 0; // Formerly: i
		int renameCost = 0;
		int totalNewEnchantLvls = 0;

		if (!itemInput1.isEmpty() && EnchantmentHelper.canHaveEnchantments(itemInput1)) {
			ItemStack itemOutput = itemInput1.copy(); // Formerly: itemStack2
			ItemStack itemInput2 = this.input.getStack(1); // Formerly: itemStack3
			ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(itemOutput));
			boolean item1HasMending = itemOutput.hasEnchantment(Enchantments.MENDING);
			this.repairItemUsage = 0;
			
			if (!itemInput2.isEmpty()) {
				boolean item2HasEnchants = itemInput2.contains(DataComponentTypes.STORED_ENCHANTMENTS); // Formerly: bl
				
				boolean item1IsNetherite = itemInput1.canRepairWith(new ItemStack(Items.NETHERITE_INGOT));
				boolean item2IsDiamond = itemInput2.isOf(Items.DIAMOND);
				boolean isRepairingNetheriteWithDiamond = item1IsNetherite && item1HasMending && item2IsDiamond; // Allow netherite items with mending to be repaired with diamonds

				if (itemOutput.isDamageable() && (itemInput1.canRepairWith(itemInput2) || isRepairingNetheriteWithDiamond)) {
					int numRepairsForMaxDmg = 4;
					boolean item2IsNetherite = itemInput2.isOf(Items.NETHERITE_INGOT);
					if (item2IsNetherite) {
						numRepairsForMaxDmg = 1; // Only require 1 netherite ingot to fully repair a netherite tool
					} else if (item1HasMending && !isRepairingNetheriteWithDiamond) {
						numRepairsForMaxDmg = 2; // Mending resource discount doesn't apply when using diamonds to repair netherite gear
					}
					int maxDmgRepair = itemOutput.getMaxDamage() / numRepairsForMaxDmg;
					int dmgRepair = Math.min(itemOutput.getDamage(), maxDmgRepair); // Formerly: k
					if (dmgRepair <= 0) {
						this.output.setStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						ci.cancel();
						return;
					}

					int repairItemsUsed; // Formerly: m
					int unitRepairCost = baseRepairCost;
					if (isRepairingNetheriteWithDiamond) {
						unitRepairCost += 2; // Charge more levels for repairing netherite gear with diamonds
					}
					for (repairItemsUsed = 0; dmgRepair > 0 && repairItemsUsed < itemInput2.getCount(); repairItemsUsed++) {
						int n = itemOutput.getDamage() - dmgRepair;
						itemOutput.setDamage(n);
						cost += unitRepairCost;
						dmgRepair = Math.min(itemOutput.getDamage(), maxDmgRepair);
					}

					this.repairItemUsage = repairItemsUsed;
				} else {
					if (!item2HasEnchants && (!itemOutput.isOf(itemInput2.getItem()) || !itemOutput.isDamageable())) {
						this.output.setStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						ci.cancel();
						return;
					}

					if (itemOutput.isDamageable() && !item2HasEnchants) {
						int item1RemainDur = itemInput1.getMaxDamage() - itemInput1.getDamage(); // Formerly: kx
						int item2RemainDur = itemInput2.getMaxDamage() - itemInput2.getDamage(); // Formerly: m
						int n = item2RemainDur + itemOutput.getMaxDamage() * 12 / 100;
						int itemOutputDur = item1RemainDur + n; // Formerly: o
						int itemOutputDmg = itemOutput.getMaxDamage() - itemOutputDur; // Formerly: p
						if (itemOutputDmg < 0) {
							itemOutputDmg = 0;
						}

						if (itemOutputDmg < itemOutput.getDamage()) {
							itemOutput.setDamage(itemOutputDmg);
							cost += baseRepairCost + 2;
						}
					}

					ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(itemInput2);
					boolean hasAcceptableEnchant = false; // Formerly: bl2
					boolean hasUnacceptableEnchant = false; // Formerly: bl3

					for (Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
						RegistryEntry<Enchantment> item2Enchant = (RegistryEntry<Enchantment>)entry.getKey(); // Formerly: registryEntry
						int item1EnchantLvl = builder.getLevel(item2Enchant); // Formerly: q
						int item2EnchantLvl = entry.getIntValue(); // Formerly: r
						item2EnchantLvl = item1EnchantLvl == item2EnchantLvl ? item2EnchantLvl + 1 : Math.max(item2EnchantLvl, item1EnchantLvl);
						Enchantment enchantment = item2Enchant.value();
						boolean isAcceptableEnchant = enchantment.isAcceptableItem(itemInput1); // Formerly: bl4
						if (this.player.getAbilities().creativeMode || itemInput1.isOf(Items.ENCHANTED_BOOK)) {
							isAcceptableEnchant = true;
						}

						for (RegistryEntry<Enchantment> item1Enchant : builder.getEnchantments()) {
							if (!item1Enchant.equals(item2Enchant) && !Enchantment.canBeCombined(item2Enchant, item1Enchant)) {
								isAcceptableEnchant = false;
								if (item2Enchant.matchesKey(Enchantments.BINDING_CURSE)) {
									// If binding curse can't be added, don't allow the items to be combined (as that would make it possible to remove curses)
									this.output.setStack(0, ItemStack.EMPTY);
									this.levelCost.set(0);
									ci.cancel();
									return;
								}
							}
						}
						
						if (!isAcceptableEnchant) {
							hasUnacceptableEnchant = true;
							cost++;
						} else {
							hasAcceptableEnchant = true;
							if (item2EnchantLvl > enchantment.getMaxLevel()) {
								item2EnchantLvl = enchantment.getMaxLevel();
							}

							builder.set(item2Enchant, item2EnchantLvl);
							totalNewEnchantLvls += item2EnchantLvl;
						}
					}
					
					if (hasUnacceptableEnchant && !hasAcceptableEnchant) {
						this.output.setStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						ci.cancel();
						return;
					}
				}
			}

			if (totalNewEnchantLvls > 0) {
				cost += baseEnchantCost;
			}

			if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
				if (!this.newItemName.equals(itemInput1.getName().getString())) {
					renameCost = 1;
					cost += renameCost;
					itemOutput.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
				}
			} else if (itemInput1.contains(DataComponentTypes.CUSTOM_NAME)) {
				renameCost = 1;
				cost += renameCost;
				itemOutput.remove(DataComponentTypes.CUSTOM_NAME);
			}
			
			this.levelCost.set(cost <= 0 ? 0 : cost);
			if (cost <= 0) {
				itemOutput = ItemStack.EMPTY;
			}

			if (renameCost == cost && renameCost > 0) {
				if (this.levelCost.get() >= 40) {
					this.levelCost.set(39);
				}

				this.keepSecondSlot = true;
			}

			if (!itemOutput.isEmpty()) {
				EnchantmentHelper.set(itemOutput, builder.build());
				int totalEnchants = EnchantmentHelper.getEnchantments(itemOutput).getEnchantments().size();
				int enchantLvlTax = totalEnchants + totalNewEnchantLvls; // Pay per enchant, but only per enchant level for newly added enchants
				if (this.repairItemUsage > 0) {
					enchantLvlTax *= this.repairItemUsage;
				} else {
					// Give more weight to the number of enchantments when not repairing
					enchantLvlTax += totalEnchants;
				}
				
				if (itemOutput.hasEnchantment(Enchantments.BINDING_CURSE) || (!BetterRepairing.isHardcore(this.player) && itemOutput.hasEnchantment(Enchantments.VANISHING_CURSE))) {
					// Give curses an upside/positive effect so that there's a tradeoff and an interesting decision to be made
					// Only applies to curse of vanishing when not in hardcore mode
					enchantLvlTax = 0;
				}
				
				this.levelCost.set(cost + enchantLvlTax);
			}

			this.output.setStack(0, itemOutput);
			this.sendContentUpdates();
		} else {
			this.output.setStack(0, ItemStack.EMPTY);
			this.levelCost.set(0);
		}

        ci.cancel();
    }
}
