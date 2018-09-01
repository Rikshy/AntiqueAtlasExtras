package de.shyrik.atlasextras.compat;

import de.shyrik.atlasextras.features.travel.AtlasHandler;
import de.shyrik.atlasextras.features.travel.TravelHandler;
import gollorum.signpost.blocks.SuperPostPost;
import gollorum.signpost.event.UpdateWaystoneEvent;
import gollorum.signpost.management.PostHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SignpostHandler implements TravelHandler.ICostHandler {

    public static final String MODID = "signpost";

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onUpdateWaystoneEvent(UpdateWaystoneEvent event) {
        BlockPos pos = new BlockPos(event.x, event.y, event.z);
        switch (event.type) {
            case PLACED:
                AtlasHandler.addMarker(event.world, pos, event.name, true, false, MODID);
                break;
            case NAMECHANGED:
                AtlasHandler.addMarker(event.world, pos, event.name, true, false, MODID);
                break;
            case DESTROYED:
                AtlasHandler.removeMarker(event.world, pos);
                break;
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onSignpostPlaced(BlockEvent.PlaceEvent event) {
        if (event.getPlacedBlock().getBlock() instanceof SuperPostPost) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            if (world.getBlockState(pos.up()).getBlock() instanceof SuperPostPost )
                AtlasHandler.removeMarker(world, pos.up());
            //No need for upstacking markers on one spot
            if (!(world.getBlockState(pos.down()).getBlock() instanceof SuperPostPost))
                AtlasHandler.addMarker(world, pos, I18n.format("atlasextras.marker.signpost"), false, true, MODID);
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onSignpostBroken(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() instanceof SuperPostPost) {
            AtlasHandler.removeMarker(event.getWorld(), event.getPos());
        }
    }

    @Override
    public boolean canPay(EntityPlayer player, BlockPos destination) {
        return PostHandler.canPay(player, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), destination.getX(), destination.getY(), destination.getZ());
    }

    @Override
    public void pay(EntityPlayer player, BlockPos destination) {
        PostHandler.doPay(player, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), destination.getX(), destination.getY(), destination.getZ());
    }
}
