package de.shyrik.atlasextras.compat;

import de.shyrik.atlasextras.common.CommonProxy;
import gollorum.signpost.blocks.SuperPostPost;
import gollorum.signpost.event.UpdateWaystoneEvent;
import hunternif.mc.atlas.api.AtlasAPI;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SignpostHandler {

    @SubscribeEvent
    @Optional.Method(modid = "signpost")
    public void onUpdateWaystoneEvent(UpdateWaystoneEvent event) {
        switch (event.type) {
            case PLACED:
                addWay(event.world, event.x, event.z, event.name);
                break;
            case NAMECHANGED:
                removeMarker(event.world, event.x, event.z);
                //addWay(event.world, event.x, event.z, event.name);
                break;
            case DESTROYED:
                removeMarker(event.world, event.x, event.z);
                break;
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = "signpost")
    public void onSignpostPlaced(BlockEvent.PlaceEvent event) {
        if(event.getPlacedBlock().getBlock() instanceof SuperPostPost){
            addPost(event.getWorld(), event.getPos().getX(), event.getPos().getZ(), I18n.format("atlasextras.marker.signpost"));
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = "signpost")
    public void onSignpostBroken(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() instanceof SuperPostPost) {
            removeMarker(event.getWorld(), event.getPos().getX(), event.getPos().getZ());
        }
    }

    private static void addPost(World world, int x, int z, String name) {
        AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, CommonProxy.MARKER_SIGNPOST.toString(), name, x, z);
    }

    private static void addWay(World world, int x, int z, String name) {
        AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, CommonProxy.MARKER_WAYSTONE.toString(), name, x, z);
    }

    private static void removeMarker(World world, int x, int z) {
    }
}
