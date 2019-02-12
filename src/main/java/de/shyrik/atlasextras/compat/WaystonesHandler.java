package de.shyrik.atlasextras.compat;

import de.shyrik.atlasextras.features.travel.AtlasHandler;
import de.shyrik.atlasextras.features.travel.TravelHandler;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.util.WaystoneActivatedEvent;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WaystonesHandler implements TravelHandler.ICostHandler {
    public static final String MODID = "waystones";

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onWaystoneActivated(WaystoneActivatedEvent event) {
        WorldServer w =FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(event.getDimension());
        FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(event.getDimension()).addScheduledTask(() ->
                AtlasHandler.addMarker(w, event.getPos(), event.getWaystoneName(), true, true, MODID)
        );
    }

    @SubscribeEvent
    @Optional.Method(modid = MODID)
    public void onWaystoneBroken(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() instanceof BlockWaystone) {
            AtlasHandler.removeMarker(event.getWorld(), event.getPos());
        }
    }

    @Override
    public void pay(EntityPlayer player, BlockPos destination) {
        int dist = (int) Math.sqrt(player.getDistanceSqToCenter(destination));
        int xpLevelCost = WaystoneConfig.general.blocksPerXPLevel > 0 ? MathHelper.clamp(dist / WaystoneConfig.general.blocksPerXPLevel, 0, WaystoneConfig.general.maximumXpCost) : 0;
        player.addExperienceLevel(-xpLevelCost);
        PlayerWaystoneHelper.setLastWarpStoneUse(player, System.currentTimeMillis());
    }

    @Override
    public boolean canTravel(EntityPlayer player, BlockPos destination) {
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
