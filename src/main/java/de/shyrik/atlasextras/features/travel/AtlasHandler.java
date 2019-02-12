package de.shyrik.atlasextras.features.travel;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.network.packet.MarkerClickPacket;
import de.shyrik.atlasextras.network.NetworkHelper;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.event.MarkerClickedEvent;
import hunternif.mc.atlas.marker.Marker;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AtlasHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onMarkerClick(MarkerClickedEvent event) {
        NetworkHelper.sendToServer(new MarkerClickPacket(event.marker.getId(), event.player));
    }

    public static void addMarker(World world, BlockPos pos, String name, boolean canTravelTo, boolean canTravelFrom, String modId) {
        ResourceLocation markerType = canTravelTo && canTravelFrom ? AtlasExtras.MARKER_TRAVEL : canTravelTo ? AtlasExtras.MARKER_TRAVELTO : AtlasExtras.MARKER_TRAVELFROM;
        MarkerMap map = MarkerMap.instance(world);
        MarkerMap.Mark mark = map.getFromPos(pos);
        if (mark != null) {
            AtlasAPI.getMarkerAPI().deleteGlobalMarker(world, mark.id);
            map.remove(mark.id);
        }
        Marker marker = AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, markerType.toString(), name, pos.getX(), pos.getZ());

        if(marker != null) map.put(marker.getId(), pos, canTravelTo, canTravelFrom, modId);
    }

    public static void removeMarker(World world, BlockPos pos) {
        MarkerMap markers = MarkerMap.instance(world);
        MarkerMap.Mark mark = markers.getFromPos(pos);
        if (mark != null) {
            AtlasAPI.getMarkerAPI().deleteGlobalMarker(world, mark.id);
            markers.remove(mark.id);
        }
    }
}
