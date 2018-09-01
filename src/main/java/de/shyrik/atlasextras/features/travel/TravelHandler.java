package de.shyrik.atlasextras.features.travel;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.core.Configuration;
import de.shyrik.atlasextras.network.NetworkHelper;
import de.shyrik.atlasextras.network.TravelEffectPacket;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TravelHandler {

    private static Map<String, ICostHandler> handlers = new HashMap<>();

    public interface ICostHandler {
        void pay(EntityPlayer player, BlockPos destination);

        boolean canTravel(EntityPlayer player, BlockPos destination);
    }

    public static void registerCostHandler(String modid, ICostHandler handler) {
        handlers.put(modid, handler);
    }

    public static void travel(World world, int markerId, String playerUUID) {
        MarkerMap map = MarkerMap.instance(world);
        MarkerMap.Mark mark = map.get(markerId);

        if (mark != null && mark.canTravelTo) {
            EntityPlayer player = world.getPlayerEntityByUUID(UUID.fromString(playerUUID));
            if (player != null && map.hasJumpNearby(player.getPosition())) {
                if (player.isCreative() || canPay(mark.sourceMod, player, mark.pos)) {
                    if (tryTravel(world, player, mark.pos)) pay(mark.sourceMod, player, mark.pos);
                    else player.sendMessage(new TextComponentTranslation("atlasextras.message.blocked"));
                }
            }
        }
    }

    private static boolean tryTravel(World world, EntityPlayer player, BlockPos pos) {
        if (player.isBeingRidden()) {
            player.removePassengers();
        }
        if (player.isRiding()) {
            player.dismountRidingEntity();
        }
        boolean flag = false;

        BlockPos targetLoc = pos;
        if (world.getBlockState(pos).getBlock() instanceof BlockHorizontal) {
            EnumFacing face = world.getBlockState(pos).getValue(BlockHorizontal.FACING);
            targetLoc = pos.offset(face);
            if (player.attemptTeleport(targetLoc.getX() + 0.5, targetLoc.getY() + 0.5, targetLoc.getZ() + 0.5)) {
                NetworkHelper.sendAround(new TravelEffectPacket(player.getPosition()), player.getPosition(), player.dimension);
                player.rotationYaw = getRotationYaw(face.getOpposite());
                flag = true;
            }
        }
        if (!flag) {
            for (EnumFacing face : EnumFacing.VALUES) {
                targetLoc = pos.offset(face);
                if (player.attemptTeleport(targetLoc.getX() + 0.5, targetLoc.getY() + 0.5, targetLoc.getZ() + 0.5)) {
                    NetworkHelper.sendAround(new TravelEffectPacket(player.getPosition()), player.getPosition(), player.dimension);
                    player.rotationYaw = getRotationYaw(face.getOpposite());
                    flag = true;
                    break;
                }
            }
        }

        if (flag) NetworkHelper.sendAround(new TravelEffectPacket(targetLoc), targetLoc, player.dimension);

        return flag;
    }

    public static float getRotationYaw(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return 180f;
            case SOUTH:
                return 0f;
            case WEST:
                return 90f;
            case EAST:
                return -90f;
        }
        return 0f;
    }

    private static void pay(String modid, EntityPlayer player, BlockPos destination) {
        if (!handlers.containsKey(modid) || Configuration.COSTPROVIDER.useOwnCostProvider)
            handlers.get(AtlasExtras.MODID).pay(player, destination);
        else handlers.get(modid).pay(player, destination);
    }

    private static boolean canPay(String modid, EntityPlayer player, BlockPos destination) {
        if (!handlers.containsKey(modid) || Configuration.COSTPROVIDER.useOwnCostProvider)
            return handlers.get(AtlasExtras.MODID).canTravel(player, destination);
        return handlers.get(modid).canTravel(player, destination);
    }
}
