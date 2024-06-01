package com.example.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import wraith.fwaystones.util.Utils;

import java.util.Iterator;

import static java.lang.Math.min;
import static net.minecraft.util.math.MathHelper.floor;
import static wraith.fwaystones.util.Utils.*;

import com.glisco.numismaticoverhaul.ModComponents;

@Mixin(value = Utils.class, remap = false)
public class UtilsMixin {
	private static Identifier copperId = new Identifier("numismatic-overhaul", "copper_coin");
	private static Identifier silverIO = new Identifier("numismatic-overhaul", "silver_coin");
	private static Identifier goldId = new Identifier("numismatic-overhaul", "gold_coin");
	private static boolean useNumismatic = getTeleportCostItem().equals(copperId) || getTeleportCostItem().equals(silverIO) || getTeleportCostItem().equals(goldId);
	private static int itemFactor = getTeleportCostItem().equals(copperId) ? 1 : getTeleportCostItem().equals(silverIO) ? 100 : 10000;

	/**
	 * @author Galysso
	 * @reason Ass ability to check coins from numismatic-overhaul purse
	 */
	@Overwrite
	public static boolean containsItem(PlayerInventory inventory, Item item, int maxAmount) {
		Identifier itemId = getTeleportCostItem();
		if (useNumismatic) {
			return maxAmount*itemFactor <= (int) ModComponents.CURRENCY.get(inventory.player).getValue();
		} else {
			int amount = 0;
			Iterator var4 = inventory.main.iterator();

			ItemStack stack;
			while (var4.hasNext()) {
				stack = (ItemStack) var4.next();
				if (stack.getItem().equals(item)) {
					amount += stack.getCount();
				}
			}

			var4 = inventory.offHand.iterator();

			while (var4.hasNext()) {
				stack = (ItemStack) var4.next();
				if (stack.getItem().equals(item)) {
					amount += stack.getCount();
				}
			}

			var4 = inventory.armor.iterator();

			while (var4.hasNext()) {
				stack = (ItemStack) var4.next();
				if (stack.getItem().equals(item)) {
					amount += stack.getCount();
				}
			}

			return amount >= maxAmount;
		}
	}

	/**
	 * @author Galysso
	 * @reason Adding ability to remove coins from numismatic-overhaul purse
	 */
	@Overwrite
	public static void removeItem(PlayerInventory inventory, Item item, int totalAmount) {
		/* check the player has enough in their purse first (what the player has on hand might be voluntarily kept on hand) */
		Identifier itemId = getTeleportCostItem();
		if (useNumismatic) {
			ModComponents.CURRENCY.get(inventory.player).modify(-itemFactor*totalAmount);
		} else {
			Iterator var3 = inventory.main.iterator();

			while (totalAmount > 0) {
				ItemStack stack;
				int amount;
				if (!var3.hasNext()) {
					var3 = inventory.offHand.iterator();

					do {
						if (!var3.hasNext()) {
							var3 = inventory.armor.iterator();

							do {
								if (!var3.hasNext()) {
									return;
								}

								stack = (ItemStack) var3.next();
								if (stack.getItem().equals(item)) {
									amount = stack.getCount();
									stack.decrement(totalAmount);
									totalAmount -= amount;
								}
							} while (totalAmount > 0);

							return;
						}

						stack = (ItemStack) var3.next();
						if (stack.getItem().equals(item)) {
							amount = stack.getCount();
							stack.decrement(totalAmount);
							totalAmount -= amount;
						}
					} while (totalAmount > 0);

					return;
				}

				stack = (ItemStack) var3.next();
				if (stack.getItem().equals(item)) {
					amount = stack.getCount();
					stack.decrement(totalAmount);
					totalAmount -= amount;
				}
			}
		}
	}
}