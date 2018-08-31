package de.shyrik.atlasextras.client;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.Configuration;
import de.shyrik.atlasextras.common.CommonProxy;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.registry.MarkerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        if(Configuration.COMPAT.enableFastTravel)
            MinecraftForge.EVENT_BUS.register(new AtlasHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        List<MarkerType> list = Stream.of(
                new MarkerType(MARKER_SIGNPOST, new ResourceLocation(AtlasExtras.MODID, "textures/markers/signpost.png")),
                new MarkerType(MARKER_WAYSTONE, new ResourceLocation(AtlasExtras.MODID, "textures/markers/waystone.png"))
        ).collect(Collectors.toList());

        for (MarkerType mt : list)
            AtlasAPI.getMarkerAPI().registerMarker(mt);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerTex(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation(AtlasExtras.MODID, "markers/signpost"));
        event.getMap().registerSprite(new ResourceLocation(AtlasExtras.MODID, "markers/waystone"));
    }
}
