package net.kyrptonaught.shulkerpick;

import net.kyrptonaught.shulkerutils.ItemStackInventory;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryHelper {
    public static boolean insertStack(Inventory fromInventory, int fromSlot, Inventory toInventory, int ignoreSlot) {
        if (attemptCombine(fromInventory, fromSlot, toInventory, ignoreSlot))
            return true;
        for (int i = 0; i < getSize(toInventory); i++) {
            if (i != ignoreSlot) {
                ItemStack invStack = toInventory.getStack(i);
                if (invStack.isEmpty()) {
                    toInventory.setStack(i, fromInventory.removeStack(fromSlot));
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean insertIntoShulker(PlayerEntity playerEntity, int fromSlot) {
        for (int i = 0; i < getSize(playerEntity.getInventory()); i++) {
            ItemStack stack = playerEntity.getInventory().getStack(i);
            if (Util.isShulkerItem(stack)) {
                ItemStackInventory shulkerInv = ShulkerUtils.getInventoryFromShulker(stack);
                if (ShulkerUtils.shulkerContainsAny(shulkerInv,playerEntity.getInventory().getStack(fromSlot))) {
                    ItemStack returnStack = shulkerInv.addStack(playerEntity.getInventory().removeStack(fromSlot));
                    playerEntity.getInventory().setStack(fromSlot, returnStack);
                    shulkerInv.onClose(playerEntity);
                    if (returnStack.isEmpty()) return true;
                }
            }
        }
        return false;
    }

    private static boolean isStackFull(ItemStack stack) {
        return stack.getCount() >= stack.getMaxCount();
    }

    private static boolean attemptCombine(Inventory fromInventory, int fromSlot, Inventory toInventory, int ignoreSlot) {
        ItemStack insertStack = fromInventory.getStack(fromSlot);
        if (!isStackFull(insertStack))
            for (int i = 0; i < getSize(toInventory); i++) {
                if (i != ignoreSlot) {
                    ItemStack invStack = toInventory.getStack(i);
                    if (isStackFull(invStack)) continue;
                    if (Util.areItemsEqual(invStack, insertStack)) {
                        combineStacks(invStack, insertStack);
                        fromInventory.setStack(fromSlot, insertStack);
                        toInventory.setStack(i, invStack);
                        if (insertStack.getCount() == 0) return true;
                    }
                }
            }
        return false;
    }

    public static int getSize(Inventory inventory) {
        if (inventory instanceof PlayerInventory)
            return Math.min(36, inventory.size());
        return inventory.size();
    }

    private static int combineStacks(ItemStack stack, ItemStack stack2) {
        int maxInsertAmount = Math.min(stack.getMaxCount() - stack.getCount(), stack2.getCount());
        stack.increment(maxInsertAmount);
        stack2.increment(-maxInsertAmount);
        return maxInsertAmount;
    }
}
