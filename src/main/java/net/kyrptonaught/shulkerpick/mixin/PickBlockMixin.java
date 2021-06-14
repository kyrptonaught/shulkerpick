package net.kyrptonaught.shulkerpick.mixin;


import net.kyrptonaught.shulkerpick.PickShulkerPacket;
import net.kyrptonaught.shulkerpick.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class PickBlockMixin {

    @Shadow
    public ClientPlayerEntity player;

    @Redirect(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getSlotWithStack(Lnet/minecraft/item/ItemStack;)I"))
    public int pickFromShulker(PlayerInventory playerInventory, ItemStack stack) {
        if (player.getAbilities().creativeMode) return 0;
        int slot = playerInventory.getSlotWithStack(stack);
        if (slot != -1) return slot;
        if (Util.getShulkerWithStack(playerInventory, stack) > -1)
            PickShulkerPacket.sendPacket(stack);
        return -1;
    }
}
