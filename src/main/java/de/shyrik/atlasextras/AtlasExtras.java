package de.shyrik.atlasextras;

import de.shyrik.atlasextras.core.IModProxy;
import de.shyrik.atlasextras.network.NetworkHelper;
import net.minecraft.util.ResourceLocation;
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
    public static final String DEPENDENCY = "required-after:antiqueatlas;after:signpost;after:waystones";

    public static final String CLIENT_PROXY = "de.shyrik.atlasextras.core.ClientProxy";
    public static final String SERVER_PROXY = "de.shyrik.atlasextras.core.CommonProxy";

    public static final String CHANNEL = "atlasextras";

    public static final ResourceLocation MARKER_TRAVELFROM = new ResourceLocation(AtlasExtras.MODID, "travelfrom");
    public static final ResourceLocation MARKER_TRAVELTO = new ResourceLocation(AtlasExtras.MODID, "travelto");
    public static final ResourceLocation MARKER_TRAVEL = new ResourceLocation(AtlasExtras.MODID, "travel");

    @SidedProxy(clientSide = AtlasExtras.CLIENT_PROXY, serverSide = AtlasExtras.SERVER_PROXY)
    public static IModProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkHelper.registerPackets();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
