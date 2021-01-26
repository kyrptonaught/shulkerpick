package net.kyrptonaught.shulkerpick;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.shulkerutils.ItemStackInventory;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.util.Identifier;

public class PickShulkerPacket {

    private static final Identifier SHULKER_PICK_PACKET = new Identifier(ShulkerPickMod.MOD_ID, "pickshulker");

    static void registerReceivePacket() {
        ServerPlayNetworking.registerGlobalReceiver(SHULKER_PICK_PACKET, (server, player, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            ItemStack stack = packetByteBuf.readItemStack();
            server.execute(() -> {
                int shulkerSlot = Util.getShulkerWithStack(player.inventory, stack);
                if (shulkerSlot != -1) {
                    ItemStackInventory shulkerInv = ShulkerUtils.getInventoryFromShulker(player.inventory.getStack(shulkerSlot));
                    int slotInShulker = Util.getSlotWithStack(shulkerInv, stack);
                    if (slotInShulker != -1) {
                        //empty hotbar slot
                        int hotbarSlot = Util.getHotBarSlot(player.inventory, shulkerSlot);
                        player.inventory.selectedSlot = hotbarSlot;
                        player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
                        ItemStack stackInShulker = shulkerInv.removeStack(slotInShulker);

                        if (!player.inventory.getStack(hotbarSlot).isEmpty()) {
                            //put hotbar slot into inv slot : attempt merge or into empty
                            if (!player.inventory.getStack(hotbarSlot).isEmpty()) {
                                InventoryHelper.insertStack(player.inventory, hotbarSlot, player.inventory, hotbarSlot);
                            }
                            //swap places in shulker
                            if (!player.inventory.getStack(hotbarSlot).isEmpty() && !Util.isShulkerItem(player.inventory.getStack(hotbarSlot))) {
                                ItemStack result = ShulkerUtils.insertIntoShulker(shulkerInv, player.inventory.getStack(hotbarSlot), player);
                                player.inventory.setStack(hotbarSlot, result);
                            }
                            //drop on ground
                            if (!player.inventory.getStack(hotbarSlot).isEmpty())
                                player.dropStack(player.inventory.removeStack(hotbarSlot));
                        }
                        player.inventory.setStack(hotbarSlot, stackInShulker);
                        shulkerInv.onClose(player);
                    }
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendPacket(ItemStack stack) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeItemStack(stack);
        ClientPlayNetworking.send(SHULKER_PICK_PACKET, new PacketByteBuf(buf));
    }
}
