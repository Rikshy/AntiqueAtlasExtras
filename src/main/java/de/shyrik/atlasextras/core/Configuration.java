package de.shyrik.atlasextras.core;

import de.shyrik.atlasextras.AtlasExtras;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = AtlasExtras.MODID, category = "")
public class Configuration {

    public static final General GENERAL = new General();
    public static final Compat COMPAT = new Compat();

    @Config.Comment("Category is obsolete if fasttravel is disabled")
    public static final CostProvider COSTPROVIDER = new CostProvider();

    public static class General {
        @Config.Comment("true if the biome info should be shown, false otherwise")
        public boolean enableBiomeInfo = true;

        @Config.Comment("true if the position info should be shown, false otherwise")
        public boolean enablePositionInfo = true;

        @Config.Comment("true if the time info should be shown, false otherwise")
        public boolean enableTimeInfo = true;

        @Config.Comment("The color to display the info in (hexadecimal)")
        public String color = "ffffff";

        @Config.Ignore
        public int RGB = Integer.parseInt(color, 16);
    }

    public static class Compat {
        @Config.Comment("true to allow fast traveling by clicking on a valid marker, false otherwise")
        public boolean enableFastTravel = true;

        @Config.Comment("true to add signposts markers, false otherwise")
        public boolean compatSignpost = true;

        @Config.Comment("true to add waystones markers, false otherwise")
        public boolean compatWaystone = true;

        @Config.Comment("The maximum amount of blocks the player can be away of a sign to travel")
        @Config.RangeInt(min = 1, max = 32)
        public int distanceToMarker = 5;
    }

    public static class CostProvider {
        @Config.Comment("true to overwrite other mods costs (only through atlas travel ofc), false disables this whole section")
        public boolean useOwnCostProvider = true;

        @Config.Comment("Amount of blocks the player can travel (0 limetless - price is the limit)")
        @Config.RangeInt(min = 0, max = 10000000)
        public int maxTravelDistance = 0;

        @Config.Comment("Unit in which travel is to pay")
        public CostUnit costUnit = CostUnit.HUNGER;

        @Config.Comment("Amount of blocks the player can travel per cost Unit (0 = just 1 unit needs to be payed)")
        @Config.RangeInt(min = 0, max = 10000000)
        public int blocksPerUnit = 0;

        @Config.Comment("If costUnit is XP - how much xp should be drained per use")
        @Config.RangeInt(min = 0, max = 10000000)
        public int xpAmount = 50;

        @Config.Comment("If costUnit is ITEM - which item is needed as pay (modid:itemname:meta)")
        public String item = "";

        @Config.Ignore
        public ItemStack itemCost = parseCfgItem(item);
    }

    public enum CostUnit {
        HUNGER(),
        XP(),
        ITEM(),
        NOTHING()
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onOnConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(AtlasExtras.MODID)) {
                ConfigManager.sync(AtlasExtras.MODID, Config.Type.INSTANCE);
                GENERAL.RGB = Integer.parseInt(GENERAL.color, 16);
                COSTPROVIDER.itemCost = parseCfgItem(COSTPROVIDER.item);
            }
        }
    }

    private static ItemStack parseCfgItem(String s) {
        String[] split = s.split(":");
        if (split.length < 2 || split.length > 3) return ItemStack.EMPTY;
        else {
            Item tmp = Item.getByNameOrId(new ResourceLocation(split[0], split[1]).toString());
            if (tmp == null) return ItemStack.EMPTY;
            if (split.length == 2) return new ItemStack(tmp);
            return new ItemStack(tmp, Integer.getInteger(split[2]));
        }
    }
}
