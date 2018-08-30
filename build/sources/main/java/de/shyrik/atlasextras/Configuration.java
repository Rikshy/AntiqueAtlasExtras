package de.shyrik.atlasextras;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = AtlasExtras.MODID)
public class Configuration {

    @Config.Name("EnableBiomeInfo")
    @Config.Comment("true if the biome info should be shown, false otherwise")
    public static boolean enableBiomeInfo = true;

    @Config.Name("EnablePositionInfo")
    @Config.Comment("true if the position info should be shown, false otherwise")
    public static boolean enablePositionInfo = true;

    @Config.Name("EnableTimeInfo")
    @Config.Comment("true if the time info should be shown, false otherwise")
    public static boolean enableTimeInfo = true;

    @Config.Name("Color")
    @Config.Comment("The color to display the info in (hexadecimal)")
    public static String color = "ffffff";

    @Config.Ignore
    public static int RGB = Integer.parseInt(color, 16);

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onOnConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(AtlasExtras.MODID)) {
                ConfigManager.sync(AtlasExtras.MODID, Config.Type.INSTANCE);
                RGB = Integer.parseInt(color, 16);
            }
        }
    }
}
