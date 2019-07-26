package de.shyrik.atlasextras.compat;

import de.shyrik.atlasextras.features.travel.AtlasHandler;
import de.shyrik.atlasextras.features.travel.TravelHandler;
import gollorum.signpost.blocks.SuperPostPost;
import gollorum.signpost.event.UpdateWaystoneEvent;
import gollorum.signpost.management.PostHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Optional.Interface(iface = "de.shyrik.atlasextras.features.travel.TravelHandler.ICostHandler", modid = WaystonesHandler.MODID)
public class SignpostHandler implements TravelHandler.ICostHandler {
    public static final String MODID = "signpost";

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onUpdateWaystoneEvent(UpdateWaystoneEvent event) {
        BlockPos pos = new BlockPos(event.x, event.y, event.z);
        switch (event.type) {
            case PLACED:
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
        if (!event.getWorld().isRemote && event.getPlacedBlock().getBlock() instanceof SuperPostPost) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();

            if (world.getBlockState(pos.up()).getBlock() instanceof SuperPostPost)
                AtlasHandler.removeMarker(world, pos.up());
            //No need for upstacking markers on one spot
            if (!(world.getBlockState(pos.down()).getBlock() instanceof SuperPostPost))
                AtlasHandler.addMarker(world, pos, "Signpost", false, true, MODID);
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onSignpostBroken(BlockEvent.BreakEvent event) {
        if (!event.getWorld().isRemote && event.getState().getBlock() instanceof SuperPostPost) {
            AtlasHandler.removeMarker(event.getWorld(), event.getPos());
        }
    }

    @Override
    public boolean canTravel(EntityPlayer player, BlockPos destination) {
        if(!PostHandler.canPay(player, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), destination.getX(), destination.getY(), destination.getZ())) {
            player.sendMessage(new TextComponentTranslation("atlasextras.message.toodayumexpensive"));
            return false;
        }
        return true;
    }

    @Override
    public void pay(EntityPlayer player, BlockPos destination) {
        PostHandler.doPay(player, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), destination.getX(), destination.getY(), destination.getZ());
    }
}
