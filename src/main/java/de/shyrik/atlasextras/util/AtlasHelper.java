package de.shyrik.atlasextras.util;

import de.shyrik.atlasextras.AtlasExtras;
import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AtlasHelper {

    public static final ResourceLocation MARKER_TRAVELFROM = new ResourceLocation(AtlasExtras.MODID, "travelfrom");
    public static final ResourceLocation MARKER_TRAVELTO = new ResourceLocation(AtlasExtras.MODID, "travelto");
    public static final ResourceLocation MARKER_TRAVEL = new ResourceLocation(AtlasExtras.MODID, "travel");

    public static void registerMarker() {
        AtlasAPI.getMarkerAPI().registerMarker(MARKER_TRAVELFROM, new MarkerType(new ResourceLocation(AtlasExtras.MODID, "textures/markers/travelfrom.png")));
        AtlasAPI.getMarkerAPI().registerMarker(MARKER_TRAVELTO, new MarkerType(new ResourceLocation(AtlasExtras.MODID, "textures/markers/travelto.png")));
        AtlasAPI.getMarkerAPI().registerMarker(MARKER_TRAVEL, new MarkerType(new ResourceLocation(AtlasExtras.MODID, "textures/markers/travel.png")));
    }

    public static int getAtlasId(PlayerEntity player) {
        Integer tmp = getPlayerAtlas(player);
        return tmp == null ? -1 : tmp;
    }

    public static Integer getPlayerAtlas(PlayerEntity player) {
        if (player == null) return null;

        if (AntiqueAtlasConfig.requiresHold.get()) {
            ItemStack stack = player.getMainHandItem();
            ItemStack stack2 = player.getOffhandItem();

            if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                return stack.getDamageValue();
            } else if (!stack2.isEmpty() && stack2.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                return stack2.getDamageValue();
            }
        } else {
            if (!AntiqueAtlasConfig.itemNeeded.get()) {
                return player.getUUID().hashCode();
            }

            ItemStack stack = player.getOffhandItem();
            if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                return stack.getDamageValue();
            }

            for (int i = 0; i < 9; i++) {
                stack = player.inventory.getItem(i);
                if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                    return stack.getDamageValue();
                }
            }
        }
        return null;
    }
}
