package de.shyrik.atlasextras.compat;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.features.travel.AtlasHandler;
import de.shyrik.atlasextras.features.travel.TravelHandler;
import net.blay09.mods.waystones.GlobalWaystones;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageEditWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Optional.Interface(iface = "net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler", modid = WaystonesHandler.MODID)
public class WaystonesHandler implements TravelHandler.ICostHandler, IMessageHandler<MessageEditWaystone, IMessage> {
    public static final String MODID = "waystones";

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
    public boolean canPay(EntityPlayer player, BlockPos destination) {
        int dist = (int) Math.sqrt(player.getDistanceSqToCenter(destination));
        int xpLevelCost = WaystoneConfig.general.blocksPerXPLevel > 0 ? MathHelper.clamp(dist / WaystoneConfig.general.blocksPerXPLevel, 0, WaystoneConfig.general.maximumXpCost) : 0;

        if (!PlayerWaystoneHelper.canUseWarpStone(player))
            return false;

        if (WaystoneConfig.general.inventoryButtonXpCost && player.experienceLevel < xpLevelCost && !PlayerWaystoneHelper.canFreeWarp(player)) {
            return false;
        }

        for (WaystoneEntry entry : PlayerWaystoneData.fromPlayer(player).getWaystones()) {
            if (entry.getPos().toLong() == destination.toLong()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public IMessage onMessage(MessageEditWaystone message, MessageContext ctx) {
        NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
            EntityPlayer player = AtlasExtras.proxy.getPlayerEntity(ctx);
            BlockPos pos = message.getPos();
            if (player.getDistance(pos.getX(), pos.getY(), pos.getZ()) > 10) {
                return;
            }
            TileEntity tileEntity = player.world.getTileEntity(pos);
            if (tileEntity instanceof TileWaystone) {
                GlobalWaystones globalWaystones = GlobalWaystones.get(player.world);
                TileWaystone tileWaystone = ((TileWaystone) tileEntity).getParent();

                if (globalWaystones.getGlobalWaystone(tileWaystone.getWaystoneName()) != null && !player.capabilities.isCreativeMode && !WaystoneConfig.general.allowEveryoneGlobal) {
                    return;
                }
                if (WaystoneConfig.general.restrictRenameToOwner && !tileWaystone.isOwner(player)) {
                    return;
                }

                String newName = message.getName();
                // Disallow %RANDOM% for non-creative players to prevent unbreakable waystone exploit
                if (newName.equals("%RANDOM%") && player.capabilities.isCreativeMode) {
                    newName = I18n.format("atlasextras.marker.waystone");
                }

                if (globalWaystones.getGlobalWaystone(newName) != null && !player.capabilities.isCreativeMode) {
                    return;
                }

                AtlasHandler.addMarker(player.world, pos, newName, true, true, MODID);
            }
        });
        return null;
    }
}
