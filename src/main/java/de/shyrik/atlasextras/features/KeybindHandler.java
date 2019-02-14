package de.shyrik.atlasextras.features;

import de.shyrik.atlasextras.core.ClientProxy;
import de.shyrik.atlasextras.core.Configuration;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static de.shyrik.atlasextras.util.AtlasHelper.getPlayerAtlas;

@Mod.EventBusSubscriber
public class KeybindHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(receiveCanceled = true)
    public static void onEvent(InputEvent.KeyInputEvent event) {
        if (ClientProxy.toggleInfo.isPressed()) {
            Configuration.HUD.toggleHUDDisplay = !Configuration.HUD.toggleHUDDisplay;
            Configuration.Save();
        }
        if (ClientProxy.openAtlas.isPressed()) {
            Integer atlasId = getPlayerAtlas(Minecraft.getMinecraft().player);
            if (atlasId != null) {
                ItemStack atlas = new ItemStack(AtlasAPI.ATLAS_ITEM);
                atlas.setItemDamage(atlasId);
                AntiqueAtlasMod.proxy.openAtlasGUI(atlas);
            }
        }
    }
}
