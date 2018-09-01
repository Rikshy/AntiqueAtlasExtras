package de.shyrik.atlasextras.features;

import de.shyrik.atlasextras.core.Configuration;
import de.shyrik.atlasextras.util.MCDateTime;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import kenkron.antiqueatlasoverlay.AAOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class OverlayHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRenderGameOverlay(RenderGameOverlayEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT && mc.world != null && !mc.gameSettings.showDebugInfo) {

            if (!AAOConfig.appearance.enabled) return;
            // Overlay must close if Atlas GUI is opened
            if (mc.currentScreen instanceof GuiAtlas) return;
            if (getPlayerAtlas(mc.player) == null) return;

            BlockPos pos = mc.player.getPosition();

            int gameWidth = event.getResolution().getScaledWidth();
            int gameHeight = event.getResolution().getScaledHeight();

            int row = 0;
            if (Configuration.GENERAL.enablePositionInfo) {
                String infoPos = "x: " + pos.getX() + " y: " + pos.getY() + " z: " + pos.getZ();
                drawInfoLine(mc, row++, gameWidth, gameHeight, infoPos);
            }
            if (Configuration.GENERAL.enableBiomeInfo) {
                String infoBiome = mc.world.getBiome(pos).getBiomeName();
                drawInfoLine(mc, row++, gameWidth, gameHeight, infoBiome);
            }
            if (Configuration.GENERAL.enableTimeInfo) {
                MCDateTime dt = new MCDateTime(mc.world.getWorldTime());

                String infoTime = String.format("%s - %02d:%02d", dt.getDayName(), dt.hour, dt.min);

                drawInfoLine(mc, row, gameWidth, gameHeight, infoTime);
            }
        }
    }

    private static void drawInfoLine(Minecraft mc, int row, int gameWidth, int gameHeight, String info) {
        int infoWidth = mc.fontRenderer.getStringWidth(info);

        int atlasX = AAOConfig.position.xPosition;
        if (AAOConfig.position.alignRight) atlasX = gameWidth - (atlasX + AAOConfig.position.width);
        int startX = atlasX + (AAOConfig.position.width / 2 - infoWidth / 2);

        int atlasY = AAOConfig.position.yPosition + 2 + row * (mc.fontRenderer.FONT_HEIGHT + 1);
        if (AAOConfig.position.alignBottom) atlasY = gameHeight - (atlasY + AAOConfig.position.height);
        int startY = atlasY + (AAOConfig.position.alignBottom ? -6 : AAOConfig.position.height);

        mc.fontRenderer.drawString(info, startX, startY, Configuration.GENERAL.RGB);
    }

    private static Integer getPlayerAtlas(EntityPlayer player) {
        if (AAOConfig.appearance.requiresHold) {
            ItemStack stack = player.getHeldItemMainhand();
            ItemStack stack2 = player.getHeldItemOffhand();

            if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                return stack.getItemDamage();
            } else if (!stack2.isEmpty() && stack2.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                return stack2.getItemDamage();
            }
        } else {
            if (!SettingsConfig.gameplay.itemNeeded) {
                return player.getUniqueID().hashCode();
            }

            ItemStack stack = player.getHeldItemOffhand();
            if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                return stack.getItemDamage();
            }

            for (int i = 0; i < 9; i++) {
                stack = player.inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                    return stack.getItemDamage();
                }
            }
        }
        return null;
    }
}
