package de.shyrik.atlasextras.features;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.atlasextras.core.Configuration;
import de.shyrik.atlasextras.util.MCDateTime;
import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static de.shyrik.atlasextras.util.AtlasHelper.getPlayerAtlas;

@Mod.EventBusSubscriber
public class OverlayHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderGameOverlay(RenderGameOverlayEvent event) {
        final Minecraft mc = Minecraft.getInstance();
        if (Configuration.HUD.toggleHUDDisplay && event.getType() == RenderGameOverlayEvent.ElementType.TEXT && mc.level != null && !mc.options.renderDebug) {

            if (!AntiqueAtlasConfig.enabled.get()) return;
            //Is minimap currently active
            if (getPlayerAtlas(mc.player) == null) return;

            boolean openAtlas = mc.screen instanceof GuiAtlas;

            float gameWidth = (float) event.getWindow().getScreenWidth();
            float gameHeight = (float) event.getWindow().getScreenHeight();
            BlockPos pos = mc.player.getPosition();

            Configuration.InfoPosition renderPos = Configuration.HUD.displayPosition;
            float scale = Configuration.HUD.textScale.scale;
            float upscale = 1 / scale;

            MatrixStack matrix = event.getMatrixStack();
            matrix.pushPose();
            matrix.scale(scale, scale, scale);
            int row = 0;
            if (Configuration.HUD.enablePositionInfo) {
                String posText = "x: " + pos.getX() + " y: " + pos.getY() + " z: " + pos.getZ();
                if (Configuration.HUD.nonVerbose) {
                    String sep = Configuration.HUD.nonVerboseSeparator.length() > 3 ? Configuration.HUD.nonVerboseSeparator.substring(0, 3) : Configuration.HUD.nonVerboseSeparator;
                    posText = pos.getX() + sep + pos.getY() + sep + pos.getZ();
                }
                TextComponent infoPos = new StringTextComponent(posText);

                if (openAtlas && renderPos != Configuration.InfoPosition.MINIMAP)
                    drawOpenAtlasInfoLine(matrix, mc.font, (GuiAtlas) mc.screen, row++, infoPos, upscale);
                else if (renderPos != Configuration.InfoPosition.OPENATLAS)
                    drawMiniMapInfoLine(matrix, mc.font, row++, gameWidth, gameHeight, infoPos, upscale);
            }
            if (Configuration.HUD.enableBiomeInfo) {
                TextComponent infoBiome = new TranslationTextComponent(Util.makeDescriptionId("biome", mc.level.getBiome(pos).getRegistryName()));

                if (openAtlas && renderPos != Configuration.InfoPosition.MINIMAP)
                    drawOpenAtlasInfoLine(matrix, mc.font, (GuiAtlas) mc.screen, row++, infoBiome, upscale);
                else if (renderPos != Configuration.InfoPosition.OPENATLAS)
                    drawMiniMapInfoLine(matrix, mc.font, row++, gameWidth, gameHeight, infoBiome, upscale);
            }
            if (Configuration.HUD.enableTimeInfo) {
                MCDateTime dt = new MCDateTime(mc.level.getGameTime());
                TextComponent infoTime = new StringTextComponent(String.format("%s - %02d:%02d", dt.getDayName(), dt.hour, dt.min));

                if (openAtlas && renderPos != Configuration.InfoPosition.MINIMAP)
                    drawOpenAtlasInfoLine(matrix, mc.font, (GuiAtlas) mc.screen, row, infoTime, upscale);
                else if (renderPos != Configuration.InfoPosition.OPENATLAS)
                    drawMiniMapInfoLine(matrix, mc.font, row, gameWidth, gameHeight, infoTime, upscale);
            }
            matrix.popPose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void drawMiniMapInfoLine(MatrixStack matrix, FontRenderer fontRenderer, int row, float gameWidth, float gameHeight, TextComponent info, float scale) {
        float infoWidth = fontRenderer.width(info);

        float atlasX = AntiqueAtlasConfig.xPosition.get() * scale;
        if (AntiqueAtlasConfig.alignRight.get()) atlasX = (gameWidth - (atlasX + AntiqueAtlasConfig.width.get())) * scale;
        float startX = (atlasX + (AntiqueAtlasConfig.width.get() * scale / 2f - infoWidth / 2f));

        float atlasY = (AntiqueAtlasConfig.yPosition.get() + 2) * scale + row * (fontRenderer.lineHeight + 1 * scale);
        if (AntiqueAtlasConfig.alignBottom.get()) atlasY = gameHeight * scale - (atlasY + AntiqueAtlasConfig.height.get() * scale);
        float startY = (atlasY + (AntiqueAtlasConfig.alignBottom.get() ? -6 : AntiqueAtlasConfig.height.get()) * scale);

        fontRenderer.draw(matrix, info, startX, startY, Configuration.HUD.RGB);
    }

    @OnlyIn(Dist.CLIENT)
    private static void drawOpenAtlasInfoLine(MatrixStack matrix, FontRenderer fontRenderer, GuiAtlas gui, int row, TextComponent info, float scale) {
        float infoWidth = fontRenderer.width(info);

        float startX = (gui.getGuiX() * scale + (GuiAtlas.WIDTH * scale / 2f - infoWidth / 2f));
        float startY = (gui.getGuiY() + 2) * scale + row * (fontRenderer.lineHeight + 1 * scale) + GuiAtlas.HEIGHT * scale;

        fontRenderer.draw(matrix, info, startX, startY, Configuration.HUD.RGB);
    }
}
