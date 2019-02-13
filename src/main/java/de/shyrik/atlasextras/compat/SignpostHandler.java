package de.shyrik.atlasextras.compat;

import de.shyrik.atlasextras.features.travel.TravelHandler;
import de.shyrik.atlasextras.network.NetworkHelper;
import de.shyrik.atlasextras.network.packet.RemoveMarkerPacket;
import de.shyrik.atlasextras.network.packet.UpdateMarkerPacket;
import gollorum.signpost.blocks.SuperPostPost;
import gollorum.signpost.event.UpdateWaystoneEvent;
import gollorum.signpost.management.PostHandler;
import net.minecraft.client.resources.I18n;
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
                NetworkHelper.sendToServer(new UpdateMarkerPacket(event.world.provider.getDimension(), pos, event.name, true, false, MODID));
                break;
            case NAMECHANGED:
                NetworkHelper.sendToServer(new UpdateMarkerPacket(event.world.provider.getDimension(), pos, event.name, true, false, MODID));
                break;
            case DESTROYED:
                NetworkHelper.sendToServer(new RemoveMarkerPacket(event.world.provider.getDimension(), pos));
                break;
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onSignpostPlaced(BlockEvent.PlaceEvent event) {
        if (event.getPlacedBlock().getBlock() instanceof SuperPostPost) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            if (world.getBlockState(pos.up()).getBlock() instanceof SuperPostPost)
                NetworkHelper.sendToServer(new RemoveMarkerPacket(world.provider.getDimension(), pos.up()));
            //No need for upstacking markers on one spot
            if (!(world.getBlockState(pos.down()).getBlock() instanceof SuperPostPost))
                NetworkHelper.sendToServer(new UpdateMarkerPacket(world.provider.getDimension(), pos, I18n.format("atlasextras.marker.signpost"), false, true, MODID));
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onSignpostBroken(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() instanceof SuperPostPost) {
            NetworkHelper.sendToServer(new RemoveMarkerPacket(event.getWorld().provider.getDimension(), event.getPos()));
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
