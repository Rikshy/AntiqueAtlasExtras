package de.shyrik.atlasextras.common;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.compat.SignpostHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public static final ResourceLocation MARKER_SIGNPOST = new ResourceLocation(AtlasExtras.MODID, "signpost");
    public static final ResourceLocation MARKER_WAYSTONE = new ResourceLocation(AtlasExtras.MODID, "waystone");

    public void preInit(FMLPreInitializationEvent event) {

        if (Loader.isModLoaded("signpost")) {
            MinecraftForge.EVENT_BUS.register(new SignpostHandler());
        }
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}
