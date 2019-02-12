package de.shyrik.atlasextras;

import de.shyrik.atlasextras.compat.SignpostHandler;
import de.shyrik.atlasextras.compat.WaystonesHandler;
import de.shyrik.atlasextras.core.Configuration;
import de.shyrik.atlasextras.core.IModProxy;
import de.shyrik.atlasextras.features.travel.AtlasExtrasCostHandler;
import de.shyrik.atlasextras.features.travel.TravelHandler;
import de.shyrik.atlasextras.network.NetworkHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AtlasExtras.MODID, name = AtlasExtras.NAME, version = AtlasExtras.VERSION, dependencies = AtlasExtras.DEPENDENCY)
public class AtlasExtras {
    public static final String MODID = "atlasextras";
    public static final String NAME = "Atlas Extras";
    public static final String VERSION = "@version@";
    public static final String DEPENDENCY = "required-after:antiqueatlas@[4.5,);after:signpost;after:waystones";

    public static final String CLIENT_PROXY = "de.shyrik.atlasextras.core.ClientProxy";
    public static final String SERVER_PROXY = "de.shyrik.atlasextras.core.ServerProxy";

    public static final String CHANNEL = "atlasextras";

    public static final ResourceLocation MARKER_TRAVELFROM = new ResourceLocation(AtlasExtras.MODID, "travelfrom");
    public static final ResourceLocation MARKER_TRAVELTO = new ResourceLocation(AtlasExtras.MODID, "travelto");
    public static final ResourceLocation MARKER_TRAVEL = new ResourceLocation(AtlasExtras.MODID, "travel");

    @SidedProxy(clientSide = AtlasExtras.CLIENT_PROXY, serverSide = AtlasExtras.SERVER_PROXY)
    public static IModProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkHelper.registerPackets();
        TravelHandler.registerCostHandler(AtlasExtras.MODID, new AtlasExtrasCostHandler());

        if (Loader.isModLoaded(SignpostHandler.MODID) && Configuration.COMPAT.compatSignpost) {
            SignpostHandler sph = new SignpostHandler();
            MinecraftForge.EVENT_BUS.register(sph);
            TravelHandler.registerCostHandler(SignpostHandler.MODID, sph);
        }

        if (Loader.isModLoaded(WaystonesHandler.MODID) && Configuration.COMPAT.compatWaystone) {
            WaystonesHandler wsh = new WaystonesHandler();
            MinecraftForge.EVENT_BUS.register(wsh);
            TravelHandler.registerCostHandler(WaystonesHandler.MODID, wsh);
        }

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
