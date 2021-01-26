package net.kyrptonaught.shulkerpick;

import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class Util {

    public static int getShulkerWithStack(PlayerInventory playerInventory, ItemStack stack) {
        for (int i = 0; i < InventoryHelper.getSize(playerInventory); i++) {
            ItemStack item = playerInventory.getStack(i);
            if (isShulkerItem(item)) {
                if (getSlotWithStack(ShulkerUtils.getInventoryFromShulker(item), stack) > -1)
                    return i;
            }
        }
        return -1;
    }

    //attempt to pick a empty, then non enchanted/ non shulker box slot, then finally any slot that isn't the one with the item
    public static int getHotBarSlot(PlayerInventory inventory, int shulkerSlot) {
        for (int i = 0; i < 9; i++) {
            if (inventory.getStack(i).isEmpty()) {
                return i;
            }
        }
        if (inventory.selectedSlot != shulkerSlot)
            return inventory.selectedSlot;
        for (int i = 0; i < 9; i++) {
            if (!inventory.getStack(i).hasEnchantments() && !isShulkerItem(inventory.getStack(i))) {
                return i;
            }
        }
        if (shulkerSlot == inventory.selectedSlot)
            return inventory.selectedSlot == 8 ? 0 : inventory.selectedSlot + 1;
        return inventory.selectedSlot;
    }

    public static boolean isShulkerItem(ItemStack item) {
        return item.getItem() instanceof BlockItem && ((BlockItem) item.getItem()).getBlock() instanceof ShulkerBoxBlock;
    }

    //Copied from net.minecraft.entity.player.PlayerInventory.getSlotWithStack
    public static int getSlotWithStack(Inventory inventory, ItemStack stack) {
        for (int i = 0; i < InventoryHelper.getSize(inventory); ++i) {
            if (!inventory.getStack(i).isEmpty() && areItemsEqual(stack, inventory.getStack(i))) {
                return i;
            }
        }
        return -1;
    }

    public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2);
    }
}
