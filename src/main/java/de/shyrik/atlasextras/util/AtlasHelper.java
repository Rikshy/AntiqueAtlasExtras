package de.shyrik.atlasextras.util;

import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import kenkron.antiqueatlasoverlay.AAOConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class AtlasHelper {

    public static int getAtlasId(EntityPlayer player) {
        Integer tmp = getPlayerAtlas(player);
        return tmp == null ? -1 : tmp;
    }

    public static Integer getPlayerAtlas(EntityPlayer player) {
        if (player == null) return null;

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
