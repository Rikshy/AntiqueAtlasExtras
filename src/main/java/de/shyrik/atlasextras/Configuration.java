package de.shyrik.atlasextras;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = AtlasExtras.MODID, category = "")
public class Configuration {

    public static final General GENERAL = new General();
    public static final Compat COMPAT = new Compat();

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
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onOnConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(AtlasExtras.MODID)) {
                ConfigManager.sync(AtlasExtras.MODID, Config.Type.INSTANCE);
                GENERAL.RGB = Integer.parseInt(GENERAL.color, 16);
            }
        }
    }
}
