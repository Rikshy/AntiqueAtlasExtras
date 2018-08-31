package de.shyrik.atlasextras.compat;

import de.shyrik.atlasextras.AtlasExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TravelHandler {

    private static Map<String, ICostHandler> handlers = new HashMap<>();

    public interface ICostHandler {
        boolean tryPay(EntityPlayer player, BlockPos destination);
    }

    public static void registerCostHandler(String modid, ICostHandler handler) {
        handlers.put(modid, handler);
    }

    public static void travel(World world, int markerId, String playerUUID) {
        MarkerMap map = MarkerMap.instance(world);
        MarkerMap.Mark mark = map.get(markerId);

        if(mark != null && mark.canJumpTo) {
            EntityPlayer player = world.getPlayerEntityByUUID(UUID.fromString(playerUUID));
            if (player != null && map.hasJumpNearby(player.getPosition())) {
                BlockPos loc = mark.pos.up();

                if (TravelHandler.tryPay( mark.modid, player, loc)) {
                    player.attemptTeleport(loc.getX(), loc.getY(), loc.getZ());
                    Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.AMBIENT, 0.4F, 1F, loc));
                }
            }
        }
    }

    private static boolean tryPay(String modid, EntityPlayer player, BlockPos destination) {
        if(!handlers.containsKey(modid))
            return handlers.get(AtlasExtras.MODID).tryPay(player, destination);
        return handlers.get(modid).tryPay(player, destination);
    }
}
