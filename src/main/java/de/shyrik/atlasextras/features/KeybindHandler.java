package de.shyrik.atlasextras.features;

import com.google.common.collect.ImmutableList;
import de.shyrik.atlasextras.core.Configuration;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.api.AtlasAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static de.shyrik.atlasextras.util.AtlasHelper.getPlayerAtlas;

@Mod.EventBusSubscriber
public class KeybindHandler {

    public static final KeyBinding toggleInfo;
    public static final KeyBinding openAtlas;
    private static final List<KeyBinding> keybinds;

    private static final String KEY_CAT = "atlasextras.keybind.category";

    static {
        keybinds = ImmutableList.of(
                toggleInfo = new KeyBinding(
                        "atlasextras.keybind.togglehud",
                        KeyConflictContext.IN_GAME,
                        InputMappings.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_SEMICOLON),
                        KEY_CAT
                ),
                openAtlas = new KeyBinding(
                        "atlasextras.keybind.openatlas",
                        KeyConflictContext.IN_GAME,
                        InputMappings.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_M),
                        KEY_CAT
                )
        );
    }

    public static void registerKeys() {
        for (KeyBinding kb : KeybindHandler.keybinds) {
            ClientRegistry.registerKeyBinding(kb);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(receiveCanceled = true)
    public static void onEvent(InputEvent.KeyInputEvent event) {
        if (toggleInfo.isDown()) {
            Configuration.HUD.toggleHUDDisplay = !Configuration.HUD.toggleHUDDisplay;
            Configuration.Save();
        }
        if (openAtlas.isDown()) {
            Integer atlasId = getPlayerAtlas(Minecraft.getInstance().player);
            if (atlasId != null) {
                ItemStack atlas = new ItemStack(AtlasAPI.getAtlasItem());
                atlas.setDamageValue(atlasId);
                AntiqueAtlasModClient.openAtlasGUI(atlas);
            }
        }
    }
}
