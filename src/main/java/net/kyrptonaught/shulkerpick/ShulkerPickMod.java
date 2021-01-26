package net.kyrptonaught.shulkerpick;

import net.fabricmc.api.ModInitializer;

public class ShulkerPickMod implements ModInitializer {
    public static final String MOD_ID = "shulkerpick";

    @Override
    public void onInitialize() {
        PickShulkerPacket.registerReceivePacket();
    }
}