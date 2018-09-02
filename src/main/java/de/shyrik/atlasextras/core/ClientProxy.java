package de.shyrik.atlasextras.core;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.features.travel.AtlasHandler;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.registry.MarkerType;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientProxy extends CommonProxy {

    public static final KeyBinding toggleInfo = new KeyBinding("atlasextras.keybind.togglehud", KeyConflictContext.IN_GAME, Keyboard.KEY_SEMICOLON, "key.categories.misc");

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ClientRegistry.registerKeyBinding(toggleInfo);
        if (Configuration.COMPAT.enableFastTravel) MinecraftForge.EVENT_BUS.register(new AtlasHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        List<MarkerType> list = Stream.of(
                new MarkerType(MARKER_TRAVELFROM, new ResourceLocation(AtlasExtras.MODID, "textures/markers/travelfrom.png")),
                new MarkerType(MARKER_TRAVELTO, new ResourceLocation(AtlasExtras.MODID, "textures/markers/travelto.png")),
                new MarkerType(MARKER_TRAVEL, new ResourceLocation(AtlasExtras.MODID, "textures/markers/travel.png"))
        ).collect(Collectors.toList());

        for (MarkerType mt : list)
            AtlasAPI.getMarkerAPI().registerMarker(mt);
    }
}
