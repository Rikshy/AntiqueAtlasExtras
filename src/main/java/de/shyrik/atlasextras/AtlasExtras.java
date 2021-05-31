package de.shyrik.atlasextras;

import de.shyrik.atlasextras.compat.SignpostHandler;
import de.shyrik.atlasextras.compat.WaystonesHandler;
import de.shyrik.atlasextras.core.Configuration;
import de.shyrik.atlasextras.features.KeybindHandler;
import de.shyrik.atlasextras.features.travel.AtlasExtrasCostHandler;
import de.shyrik.atlasextras.features.travel.AtlasHandler;
import de.shyrik.atlasextras.features.travel.TravelHandler;
import de.shyrik.atlasextras.network.NetworkHelper;
import de.shyrik.atlasextras.util.AtlasHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AtlasExtras.MODID)
public class AtlasExtras {
    public static final String MODID = "atlasextras";

    public static final String CHANNEL = "atlasextras";

    public AtlasExtras() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }

    public void setup(FMLCommonSetupEvent event) {
        NetworkHelper.registerPackets();
        TravelHandler.registerCostHandler(AtlasExtras.MODID, new AtlasExtrasCostHandler());

        if (ModList.get().isLoaded(SignpostHandler.MODID) && Configuration.COMPAT.compatSignpost) {
            SignpostHandler sph = new SignpostHandler();
            MinecraftForge.EVENT_BUS.register(sph);
            TravelHandler.registerCostHandler(SignpostHandler.MODID, sph);
        }

        if (ModList.get().isLoaded(WaystonesHandler.MODID) && Configuration.COMPAT.compatWaystone) {
            WaystonesHandler wsh = new WaystonesHandler();
            MinecraftForge.EVENT_BUS.register(wsh);
            TravelHandler.registerCostHandler(WaystonesHandler.MODID, wsh);
        }
    }

    public void setupClient(FMLClientSetupEvent event) {
        if (Configuration.COMPAT.enableFastTravel)
            MinecraftForge.EVENT_BUS.register(new AtlasHandler());

        MinecraftForge.EVENT_BUS.register(new KeybindHandler());

        AtlasHelper.registerMarker();
    }
}
