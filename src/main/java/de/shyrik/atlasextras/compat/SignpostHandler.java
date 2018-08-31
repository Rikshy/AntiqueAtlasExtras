package de.shyrik.atlasextras.compat;

import de.shyrik.atlasextras.client.AtlasHandler;
import gollorum.signpost.blocks.SuperPostPost;
import gollorum.signpost.event.UpdateWaystoneEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SignpostHandler {

    @SubscribeEvent
    @Optional.Method(modid = "signpost")
    public void onUpdateWaystoneEvent(UpdateWaystoneEvent event) {
        BlockPos pos = new BlockPos(event.x, event.y, event.z);
        switch (event.type) {
            case PLACED:
                AtlasHandler.addMarker(event.world, pos, event.name, true, false);
                break;
            case NAMECHANGED:
                AtlasHandler.removeMarker(event.world, pos);
                AtlasHandler.addMarker(event.world, pos, event.name, true, false);
                break;
            case DESTROYED:
                AtlasHandler.removeMarker(event.world, pos);
                break;
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = "signpost")
    public void onSignpostPlaced(BlockEvent.PlaceEvent event) {
        if(event.getPlacedBlock().getBlock() instanceof SuperPostPost){
            AtlasHandler.addMarker(event.getWorld(), event.getPos(), I18n.format("atlasextras.marker.signpost"), false, true);
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = "signpost")
    public void onSignpostBroken(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() instanceof SuperPostPost) {
            AtlasHandler.removeMarker(event.getWorld(), event.getPos());
        }
    }
}
