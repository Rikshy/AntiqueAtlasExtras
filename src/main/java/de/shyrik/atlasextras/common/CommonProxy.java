package de.shyrik.atlasextras.common;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.Configuration;
import de.shyrik.atlasextras.compat.AtlasExtrasCostHandler;
import de.shyrik.atlasextras.compat.SignpostHandler;
import de.shyrik.atlasextras.compat.TravelHandler;
import de.shyrik.atlasextras.network.NetworkHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {

    public static final ResourceLocation MARKER_SIGNPOST = new ResourceLocation(AtlasExtras.MODID, "signpost");
    public static final ResourceLocation MARKER_WAYSTONE = new ResourceLocation(AtlasExtras.MODID, "waystone");

    public void preInit(FMLPreInitializationEvent event) {
        TravelHandler.registerCostHandler(AtlasExtras.MODID, new AtlasExtrasCostHandler());
        NetworkHelper.registerPackets();

        if (Loader.isModLoaded("signpost") && Configuration.COMPAT.compatSignpost) {
            MinecraftForge.EVENT_BUS.register(new SignpostHandler());
        }
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    /**
     * Returns a side-appropriate EntityPlayer for use during message handling
     */
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }
}
