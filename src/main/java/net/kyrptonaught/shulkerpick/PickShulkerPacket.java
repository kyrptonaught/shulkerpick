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
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.util.Identifier;

public class PickShulkerPacket {

    private static final Identifier SHULKER_PICK_PACKET = new Identifier(ShulkerPickMod.MOD_ID, "pickshulker");

    static void registerReceivePacket() {
        ServerPlayNetworking.registerGlobalReceiver(SHULKER_PICK_PACKET, (server, player, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            ItemStack stack = packetByteBuf.readItemStack();
            server.execute(() -> {
                int shulkerSlot = Util.getShulkerWithStack(player.getInventory(), stack);
                if (shulkerSlot != -1) {
                    ItemStackInventory shulkerInv = ShulkerUtils.getInventoryFromShulker(player.getInventory().getStack(shulkerSlot));
                    int slotInShulker = Util.getSlotWithStack(shulkerInv, stack);
                    if (slotInShulker != -1) {
                        //empty hotbar slot
                        int hotbarSlot = Util.getHotBarSlot(player.getInventory(), shulkerSlot);
                        player.getInventory().selectedSlot = hotbarSlot;
                        player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(player.getInventory().selectedSlot));
                        ItemStack stackInShulker = shulkerInv.removeStack(slotInShulker);

                        if (!player.getInventory().getStack(hotbarSlot).isEmpty()) {
                            //put hotbar slot into inv slot : attempt merge or into empty
                            if (!player.getInventory().getStack(hotbarSlot).isEmpty()) {
                                InventoryHelper.insertStack(player.getInventory(), hotbarSlot, player.getInventory(), hotbarSlot);
                            }
                            //swap places in shulker
                            if (!player.getInventory().getStack(hotbarSlot).isEmpty() && !Util.isShulkerItem(player.getInventory().getStack(hotbarSlot))) {
                                ItemStack result = ShulkerUtils.insertIntoShulker(shulkerInv, player.getInventory().getStack(hotbarSlot), player);
                                player.getInventory().setStack(hotbarSlot, result);
                            }
                            //drop on ground
                            if (!player.getInventory().getStack(hotbarSlot).isEmpty())
                                player.dropStack(player.getInventory().removeStack(hotbarSlot));
                        }
                        player.getInventory().setStack(hotbarSlot, stackInShulker);
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
