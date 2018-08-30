package de.shyrik.atlasextras;

import de.shyrik.atlasextras.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = AtlasExtras.MODID, name = AtlasExtras.NAME, version = AtlasExtras.VERSION, dependencies = AtlasExtras.DEPENDENCY)
public class AtlasExtras
{
    public static final String MODID = "atlasextras";
    public static final String NAME = "Atlas Extras";
    public static final String VERSION = "@version@";
    public static final String DEPENDENCY = "required-after:antiqueatlas;after:signpost;";

    public static final String CLIENT_PROXY = "de.shyrik.atlasextras.client.ClientProxy";
    public static final String SERVER_PROXY = "de.shyrik.atlasextras.common.CommonProxy";

    @SidedProxy(clientSide = AtlasExtras.CLIENT_PROXY, serverSide = AtlasExtras.SERVER_PROXY)
    public static CommonProxy proxy;

    @Mod.Instance
    public static AtlasExtras instance;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
