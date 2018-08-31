package de.shyrik.atlasextras.features.travel;

import de.shyrik.atlasextras.core.CommonProxy;
import de.shyrik.atlasextras.network.MarkerClickPacket;
import de.shyrik.atlasextras.network.NetworkHelper;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.event.MarkerClickedEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AtlasHandler {

    @SubscribeEvent
    public void onMarkerClick(MarkerClickedEvent event) {
        NetworkHelper.sendToServer(new MarkerClickPacket(event.marker.getId(), event.player));
    }

    public static void addMarker(World world, BlockPos pos, String name, boolean canJumpTo, boolean canJumpFrom, String modid) {
        ResourceLocation markerType = canJumpTo ? CommonProxy.MARKER_WAYSTONE : CommonProxy.MARKER_SIGNPOST;
        int id = AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, markerType.toString() , name, pos.getX(), pos.getZ());
        MarkerMap.instance(world).put(id, pos, canJumpTo, canJumpFrom, modid);
    }

    public static void removeMarker(World world, BlockPos pos) {
        MarkerMap markers = MarkerMap.instance(world);
        MarkerMap.Mark mark = markers.getFromPos(pos);
        if(mark != null) {
            AtlasAPI.getMarkerAPI().deleteGlobalMarker(world, mark.id);
            markers.remove(mark.id);
        }
    }
}
