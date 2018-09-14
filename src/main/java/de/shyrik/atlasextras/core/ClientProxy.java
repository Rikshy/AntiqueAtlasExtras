package de.shyrik.atlasextras.core;

import com.google.common.collect.ImmutableList;
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

public class ClientProxy implements IModProxy {

    public static final KeyBinding toggleInfo;
    public static final KeyBinding openAtlas;
    private static final List<KeyBinding> keybinds;

    private static final String KEY_CAT = "atlasextras.keybind.category";

    static {
        keybinds = ImmutableList.of(
                toggleInfo = new KeyBinding("atlasextras.keybind.togglehud", KeyConflictContext.IN_GAME, Keyboard.KEY_SEMICOLON, KEY_CAT),
                openAtlas = new KeyBinding("atlasextras.keybind.openatlas", KeyConflictContext.IN_GAME, Keyboard.KEY_M, KEY_CAT)
        );
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (Configuration.COMPAT.enableFastTravel) MinecraftForge.EVENT_BUS.register(new AtlasHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        for (KeyBinding kb : keybinds) {
            ClientRegistry.registerKeyBinding(kb);
        }

        List<MarkerType> list = Stream.of(
                new MarkerType(AtlasExtras.MARKER_TRAVELFROM, new ResourceLocation(AtlasExtras.MODID, "textures/markers/travelfrom.png")),
                new MarkerType(AtlasExtras.MARKER_TRAVELTO, new ResourceLocation(AtlasExtras.MODID, "textures/markers/travelto.png")),
                new MarkerType(AtlasExtras.MARKER_TRAVEL, new ResourceLocation(AtlasExtras.MODID, "textures/markers/travel.png"))
        ).collect(Collectors.toList());

        for (MarkerType mt : list)
            AtlasAPI.getMarkerAPI().registerMarker(mt);
    }
}
