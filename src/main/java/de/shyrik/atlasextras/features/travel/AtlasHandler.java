package de.shyrik.atlasextras.features.travel;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.network.NetworkHelper;
import de.shyrik.atlasextras.network.packet.MarkerClickPacket;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.MarkerAPI;
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
        NetworkHelper.sendToServer(new MarkerClickPacket(Math.abs(event.marker.getId()), event.player));
    }

    public static void addMarker(World world, BlockPos pos, String name, boolean canTravelTo, boolean canTravelFrom, String modId) {
        ResourceLocation markerType = canTravelTo && canTravelFrom ? AtlasExtras.MARKER_TRAVEL : canTravelTo ? AtlasExtras.MARKER_TRAVELTO : AtlasExtras.MARKER_TRAVELFROM;
        MarkerMap map = MarkerMap.instance(world);
        MarkerAPI api = AtlasAPI.getMarkerAPI();
        MarkerMap.Mark mark = map.getFromPos(pos);

        if (mark != null) {
            api.deleteGlobalMarker(world, mark.id);
            map.remove(mark.id);
        }

        Marker marker = api.putGlobalMarker(world, false, markerType.toString(), name, pos.getX(), pos.getZ());

        if(marker != null) map.put(marker.getId(), pos, canTravelTo, canTravelFrom, modId);
    }

    public static void removeMarker(World world, BlockPos pos) {
        removeMarker(world, pos, 1);
    }

    public static void removeMarker(World world, BlockPos pos, int blockHeight) {
        MarkerMap markers = MarkerMap.instance(world);
        MarkerMap.Mark mark = markers.getFromPos(pos);
        if (mark == null) {
            int i = 0;
            while (++i < blockHeight && mark == null) {
                mark = markers.getFromPos(pos.down(i));
            }
        }

        if (mark == null) return;

        AtlasAPI.getMarkerAPI().deleteGlobalMarker(world, mark.id);
        markers.remove(mark.id);
    }
}
