package dev.agnor99.circular;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void event(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof InventoryScreen inventoryScreen && Math.random() < CircularConfig.CHANCE_FOR_SCREEN.get()) {
            event.setNewScreen(new CircularScreen(inventoryScreen.getMenu(), Minecraft.getInstance().player.getInventory(), inventoryScreen.getTitle()));
        }
    }
}
