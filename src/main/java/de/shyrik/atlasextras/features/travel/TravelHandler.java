package de.shyrik.atlasextras.features.travel;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.core.Configuration;
import de.shyrik.atlasextras.network.NetworkHelper;
import de.shyrik.atlasextras.network.packet.TravelEffectPacket;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TravelHandler {

    private static final Map<String, ICostHandler> handlers = new HashMap<>();

    public interface ICostHandler {
        void pay(PlayerEntity player, BlockPos destination);

        boolean canTravel(PlayerEntity player, BlockPos destination);
    }

    public static void registerCostHandler(String modid, ICostHandler handler) {
        handlers.put(modid, handler);
    }

    public static void travel(World world, int markerId, String playerUUID) {
        MarkerMap map = MarkerMap.instance(world);
        PlayerEntity player = world.getPlayerByUUID(UUID.fromString(playerUUID));
        MarkerMap.Mark mark = map.get(markerId);

        if (mark != null && mark.canTravelTo) {
            if (player != null && map.hasJumpNearby(player.getPosition())) {
                if (player.isCreative() || canTravel(mark.sourceMod, player, mark.pos)) {
                    if (tryTravel(world, player, mark.pos)) pay(mark.sourceMod, player, mark.pos);
                    else player.sendMessage(new TranslationTextComponent("atlasextras.message.blocked"), UUID.randomUUID());
                }
            }
        }
    }

    private static boolean tryTravel(World world, PlayerEntity player, BlockPos pos) {
        player.stopRiding();
        player.unRide();

        boolean flag = false;

        BlockPos targetLoc = pos;
        if (world.getBlockState(pos).getBlock() instanceof HorizontalBlock) {
            Direction face = world.getBlockState(pos).getValue(HorizontalBlock.FACING);
            targetLoc = pos.relative(face);
            if (player.randomTeleport(targetLoc.getX() + 0.5, targetLoc.getY() + 0.5, targetLoc.getZ() + 0.5, true)) {
                NetworkHelper.sendAround(new TravelEffectPacket(player.getPosition()), player.getPosition(), player.dimension);
                player.xRot = getRotationYaw(face.getOpposite());
                flag = true;
            }
        }
        if (!flag) {
            for (Direction face : Direction.values()) {
                targetLoc = pos.offset(face);
                if (player.randomTeleport(targetLoc.getX() + 0.5, targetLoc.getY() + 0.5, targetLoc.getZ() + 0.5, true)) {
                    NetworkHelper.sendAround(new TravelEffectPacket(player.getPosition()), player.getPosition(), player.dimension);
                    player.xRot = getRotationYaw(face.getOpposite());
                    flag = true;
                    break;
                }
            }
        }

        if (flag) NetworkHelper.sendAround(new TravelEffectPacket(targetLoc), targetLoc, player.dimension);

        return flag;
    }

    public static float getRotationYaw(Direction facing) {
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

    private static void pay(String modid, PlayerEntity player, BlockPos destination) {
        if (!handlers.containsKey(modid) || Configuration.COSTPROVIDER.useOwnCostProvider)
            handlers.get(AtlasExtras.MODID).pay(player, destination);
        else handlers.get(modid).pay(player, destination);
    }

    private static boolean canTravel(String modid, PlayerEntity player, BlockPos destination) {
        if (!handlers.containsKey(modid) || Configuration.COSTPROVIDER.useOwnCostProvider)
            return handlers.get(AtlasExtras.MODID).canTravel(player, destination);
        return handlers.get(modid).canTravel(player, destination);
    }
}
