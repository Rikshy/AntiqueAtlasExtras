package de.shyrik.atlasextras.features;

import de.shyrik.atlasextras.core.Configuration;
import de.shyrik.atlasextras.util.MCDateTime;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import kenkron.antiqueatlasoverlay.AAOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static de.shyrik.atlasextras.util.AtlasHelper.getPlayerAtlas;

@Mod.EventBusSubscriber
public class OverlayHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRenderGameOverlay(RenderGameOverlayEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (Configuration.HUD.toggleHUDDisplay && event.getType() == RenderGameOverlayEvent.ElementType.TEXT && mc.world != null && !mc.gameSettings.showDebugInfo) {

            if (!AAOConfig.appearance.enabled) return;
            //Is minimap currently active
            if (getPlayerAtlas(mc.player) == null) return;

            boolean openAtlas = mc.currentScreen instanceof GuiAtlas;

            float gameWidth = (float) event.getResolution().getScaledWidth_double();
            float gameHeight = (float) event.getResolution().getScaledHeight_double();
            BlockPos pos = mc.player.getPosition();

            Configuration.InfoPosition renderPos = Configuration.HUD.displayPosition;
            float scale = Configuration.HUD.textScale.scale;
            float upscale = 1 / scale;

            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            int row = 0;
            if (Configuration.HUD.enablePositionInfo) {
                String infoPos = "x: " + pos.getX() + " y: " + pos.getY() + " z: " + pos.getZ();
                if (Configuration.HUD.nonVerbose) {
                    String sep = Configuration.HUD.nonVerboseSeparator.length() > 3 ? Configuration.HUD.nonVerboseSeparator.substring(0, 3) : Configuration.HUD.nonVerboseSeparator;
                    infoPos = pos.getX() + sep + pos.getY() + sep + pos.getZ();
                }

                if (openAtlas && renderPos != Configuration.InfoPosition.MINIMAP)
                    drawOpenAtlasInfoLine(mc.fontRenderer, (GuiAtlas) mc.currentScreen, row++, infoPos, upscale);
                else if (renderPos != Configuration.InfoPosition.OPENATLAS)
                    drawMiniMapInfoLine(mc.fontRenderer, row++, gameWidth, gameHeight, infoPos, upscale);
            }
            if (Configuration.HUD.enableBiomeInfo) {
                String infoBiome = mc.world.getBiome(pos).getBiomeName();

                if (openAtlas && renderPos != Configuration.InfoPosition.MINIMAP)
                    drawOpenAtlasInfoLine(mc.fontRenderer, (GuiAtlas) mc.currentScreen, row++, infoBiome, upscale);
                else if (renderPos != Configuration.InfoPosition.OPENATLAS)
                    drawMiniMapInfoLine(mc.fontRenderer, row++, gameWidth, gameHeight, infoBiome, upscale);
            }
            if (Configuration.HUD.enableTimeInfo) {
                MCDateTime dt = new MCDateTime(mc.world.getWorldTime());
                String infoTime = String.format("%s - %02d:%02d", dt.getDayName(), dt.hour, dt.min);

                if (openAtlas && renderPos != Configuration.InfoPosition.MINIMAP)
                    drawOpenAtlasInfoLine(mc.fontRenderer, (GuiAtlas) mc.currentScreen, row, infoTime, upscale);
                else if (renderPos != Configuration.InfoPosition.OPENATLAS)
                    drawMiniMapInfoLine(mc.fontRenderer, row, gameWidth, gameHeight, infoTime, upscale);
            }
            GlStateManager.popMatrix();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void drawMiniMapInfoLine(FontRenderer fontRenderer, int row, float gameWidth, float gameHeight, String info, float scale) {
        float infoWidth = fontRenderer.getStringWidth(info);

        float atlasX = AAOConfig.position.xPosition * scale;
        if (AAOConfig.position.alignRight) atlasX = (gameWidth - (atlasX + AAOConfig.position.width)) * scale;
        float startX = (atlasX + (AAOConfig.position.width * scale / 2f - infoWidth / 2f));

        float atlasY = (AAOConfig.position.yPosition + 2) * scale + row * (fontRenderer.FONT_HEIGHT + 1 * scale);
        if (AAOConfig.position.alignBottom) atlasY = gameHeight * scale - (atlasY + AAOConfig.position.height * scale);
        float startY = (atlasY + (AAOConfig.position.alignBottom ? -6 : AAOConfig.position.height) * scale);

        fontRenderer.drawString(info, startX, startY, Configuration.HUD.RGB, false);
    }

    @SideOnly(Side.CLIENT)
    private static void drawOpenAtlasInfoLine(FontRenderer fontRenderer, GuiAtlas gui, int row, String info, float scale) {
        float infoWidth = fontRenderer.getStringWidth(info);

        float startX = (gui.getGuiX() * scale + (GuiAtlas.WIDTH * scale / 2f - infoWidth / 2f));
        float startY = (gui.getGuiY() + 2) * scale + row * (fontRenderer.FONT_HEIGHT + 1 * scale) + GuiAtlas.HEIGHT * scale;

        fontRenderer.drawString(info, startX, startY, Configuration.HUD.RGB, false);
    }
}
