package de.shyrik.atlasextras.compat;

import de.shyrik.atlasextras.features.travel.AtlasHandler;
import de.shyrik.atlasextras.features.travel.TravelHandler;
import de.shyrik.atlasextras.network.NetworkHelper;
import de.shyrik.atlasextras.network.packet.UpdateMarkerPacket;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


@Optional.Interface(iface = "de.shyrik.atlasextras.features.travel.TravelHandler.ICostHandler", modid = WaystonesHandler.MODID)
public class WaystonesHandler implements TravelHandler.ICostHandler {
    public static final String MODID = "waystones";

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onWaystoneActivated(WaystoneActivatedEvent event) {
        NetworkHelper.sendToServer(new UpdateMarkerPacket(event.getDimension(), event.getPlayer().blockPosition(), event.getWaystone().getName(), true, true, MODID));
    }

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onWaystoneBroken(BlockEvent.BreakEvent event) {
        if (!event.getWorld().isClientSide() && event.getState().getBlock() instanceof WaystoneBlock) {
            AtlasHandler.removeMarker(event.getWorld(), event.getPos(), 2);
        }
    }

    @Override
    public void pay(PlayerEntity player, BlockPos destination) {
        int dist = (int) Math.sqrt(player.getDistanceSqToCenter(destination));
        int xpLevelCost = WaystoneConfig.general.blocksPerXPLevel > 0 ? MathHelper.clamp(dist / WaystoneConfig.general.blocksPerXPLevel, 0, WaystoneConfig.general.maximumXpCost) : 0;
        player.addExperienceLevel(-xpLevelCost);
        PlayerWaystoneHelper.setLastWarpStoneUse(player, System.currentTimeMillis());
    }

    @Override
    public boolean canTravel(PlayerEntity player, BlockPos destination) {
        int dist = (int) Math.sqrt(player.getDistanceSqToCenter(destination));
        int xpLevelCost = WaystoneConfig.general.blocksPerXPLevel > 0 ? MathHelper.clamp(dist / WaystoneConfig.general.blocksPerXPLevel, 0, WaystoneConfig.general.maximumXpCost) : 0;

        if (!PlayerWaystoneHelper.canUseWarpStone(player)) {
            player.sendMessage(new TextComponentTranslation("atlasextras.message.cooldown"));
            return false;
        }

        if (WaystoneConfig.general.inventoryButtonXpCost && player.experienceLevel < xpLevelCost && !PlayerWaystoneHelper.canFreeWarp(player)) {
            player.sendMessage(new TextComponentTranslation("atlasextras.message.toodayumexpensive"));
            return false;
        }

        for (WaystoneEntry entry : PlayerWaystoneData.fromPlayer(player).getWaystones()) {
            if (entry.getPos().toLong() == destination.toLong()) {
                return true;
            }
        }

        player.sendMessage(new TextComponentTranslation("atlasextras.message.waypointnotknown"));
        return false;
    }
}
