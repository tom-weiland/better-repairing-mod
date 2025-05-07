package net.tomweiland.betterrepairing.mixin;

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
import net.minecraft.util.math.MathHelper;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

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
    private static int getNextCost(int cost)
    {
        return 0;
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void onUpdateResult(CallbackInfo ci) {
		// Based on Minecraft's AnvilScreenHandler.updateResult method
        ItemStack itemInput1 = this.input.getStack(0); // Formerly: itemStack1
		this.keepSecondSlot = false;
		this.levelCost.set(1);
		int cost = 0; // Formerly: i
		long repairCost = 0L; // Formerly: l
		int renameCost = 0;
		if (!itemInput1.isEmpty() && EnchantmentHelper.canHaveEnchantments(itemInput1)) {
			ItemStack itemOuput = itemInput1.copy(); // Formerly: itemStack2
			ItemStack itemInput2 = this.input.getStack(1); // Formerly: itemStack3
			ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(itemOuput));
			repairCost += (long)itemInput1.getOrDefault(DataComponentTypes.REPAIR_COST, 0).intValue() + itemInput2.getOrDefault(DataComponentTypes.REPAIR_COST, 0).intValue();
			this.repairItemUsage = 0;
			if (!itemInput2.isEmpty()) {
				boolean hasEnchants = itemInput2.contains(DataComponentTypes.STORED_ENCHANTMENTS); // Formerly: bl
				if (itemOuput.isDamageable() && itemInput1.canRepairWith(itemInput2)) {
					int maxDmgRepair = Math.min(itemOuput.getDamage(), itemOuput.getMaxDamage() / 4); // Formerly: k
					if (maxDmgRepair <= 0) {
						this.output.setStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					int repairItemsUsed; // Formerly: m
					for (repairItemsUsed = 0; maxDmgRepair > 0 && repairItemsUsed < itemInput2.getCount(); repairItemsUsed++) {
						int n = itemOuput.getDamage() - maxDmgRepair;
						itemOuput.setDamage(n);
						cost++;
						maxDmgRepair = Math.min(itemOuput.getDamage(), itemOuput.getMaxDamage() / 4);
					}

					this.repairItemUsage = repairItemsUsed;
				} else {
					if (!hasEnchants && (!itemOuput.isOf(itemInput2.getItem()) || !itemOuput.isDamageable())) {
						this.output.setStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					if (itemOuput.isDamageable() && !hasEnchants) {
						int item1RemainDur = itemInput1.getMaxDamage() - itemInput1.getDamage(); // Formerly: kx
						int item2RemainDur = itemInput2.getMaxDamage() - itemInput2.getDamage(); // Formerly: m
						int n = item2RemainDur + itemOuput.getMaxDamage() * 12 / 100;
						int itemOutputDur = item1RemainDur + n; // Formerly: o
						int itemOutputDmg = itemOuput.getMaxDamage() - itemOutputDur; // Formerly: p
						if (itemOutputDmg < 0) {
							itemOutputDmg = 0;
						}

						if (itemOutputDmg < itemOuput.getDamage()) {
							itemOuput.setDamage(itemOutputDmg);
							cost += 2;
						}
					}

					ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(itemInput2);
					boolean hasAcceptableEnchant = false; // Formerly: bl2
					boolean hasUnacceptableEnchant = false; // Formerly: bl3

					for (Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
						RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
						int item1EnchantLvl = builder.getLevel(registryEntry); // Formerly: q
						int item2EnchantLvl = entry.getIntValue(); // Formerly: r
						item2EnchantLvl = item1EnchantLvl == item2EnchantLvl ? item2EnchantLvl + 1 : Math.max(item2EnchantLvl, item1EnchantLvl);
						Enchantment enchantment = registryEntry.value();
						boolean isAcceptableEnchant = enchantment.isAcceptableItem(itemInput1); // Formerly: bl4
						if (this.player.getAbilities().creativeMode || itemInput1.isOf(Items.ENCHANTED_BOOK)) {
							isAcceptableEnchant = true;
						}

						for (RegistryEntry<Enchantment> registryEntry2 : builder.getEnchantments()) {
							if (!registryEntry2.equals(registryEntry) && !Enchantment.canBeCombined(registryEntry, registryEntry2)) {
								isAcceptableEnchant = false;
								cost++;
							}
						}

						if (!isAcceptableEnchant) {
							hasUnacceptableEnchant = true;
						} else {
							hasAcceptableEnchant = true;
							if (item2EnchantLvl > enchantment.getMaxLevel()) {
								item2EnchantLvl = enchantment.getMaxLevel();
							}

							builder.set(registryEntry, item2EnchantLvl);
							int enchantAnvilCost = enchantment.getAnvilCost(); // Formerly: s
							if (hasEnchants) {
								enchantAnvilCost = Math.max(1, enchantAnvilCost / 2);
							}

							cost += enchantAnvilCost * item2EnchantLvl;
							if (itemInput1.getCount() > 1) {
								cost = 40;
							}
						}
					}

					if (hasUnacceptableEnchant && !hasAcceptableEnchant) {
						this.output.setStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}
				}
			}

			if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
				if (!this.newItemName.equals(itemInput1.getName().getString())) {
					renameCost = 1;
					cost += renameCost;
					itemOuput.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
				}
			} else if (itemInput1.contains(DataComponentTypes.CUSTOM_NAME)) {
				renameCost = 1;
				cost += renameCost;
				itemOuput.remove(DataComponentTypes.CUSTOM_NAME);
			}

			int t = cost <= 0 ? 0 : (int)MathHelper.clamp(repairCost + cost, 0L, 2147483647L);
			this.levelCost.set(t);
			if (cost <= 0) {
				itemOuput = ItemStack.EMPTY;
			}

			if (renameCost == cost && renameCost > 0) {
				if (this.levelCost.get() >= 40) {
					this.levelCost.set(39);
				}

				this.keepSecondSlot = true;
			}

			if (this.levelCost.get() >= 40 && !this.player.getAbilities().creativeMode) {
				itemOuput = ItemStack.EMPTY;
			}

			if (!itemOuput.isEmpty()) {
				int outputCost = itemOuput.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
				if (outputCost < itemInput2.getOrDefault(DataComponentTypes.REPAIR_COST, 0)) {
					outputCost = itemInput2.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
				}

				if (renameCost != cost || renameCost == 0) {
					outputCost = getNextCost(outputCost);
				}

				itemOuput.set(DataComponentTypes.REPAIR_COST, outputCost);
				EnchantmentHelper.set(itemOuput, builder.build());
			}

			this.output.setStack(0, itemOuput);
			this.sendContentUpdates();
		} else {
			this.output.setStack(0, ItemStack.EMPTY);
			this.levelCost.set(0);
		}

        ci.cancel();
    }
}
