package de.shyrik.atlasextras.network;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.compat.MarkerMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MarkerClickPacket implements IMessage, IMessageHandler<MarkerClickPacket, IMessage> {

    public MarkerClickPacket() {}

    public MarkerClickPacket(int markderId, EntityPlayer player) {
        this.markderId = markderId;
        this.playerId = player.getUniqueID().toString();
    }

    private int markderId;
    private String playerId;

    @Override
    public void fromBytes(ByteBuf buf) {
        markderId = buf.readInt();
        playerId = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(markderId);
        ByteBufUtils.writeUTF8String(buf, playerId);
    }

    @Override
    public IMessage onMessage(MarkerClickPacket message, MessageContext ctx) {
        World world = AtlasExtras.proxy.getPlayerEntity(ctx).world;
        MarkerMap map = MarkerMap.instance(world);
        MarkerMap.Mark mark = map.get(message.markderId);

        if(mark != null && mark.canJumpTo) {
            EntityPlayer player = world.getPlayerEntityByUUID(UUID.fromString(message.playerId));
            if (player != null && map.hasJumpNearby(player.getPosition())) {
                BlockPos loc = mark.pos.up();
                player.attemptTeleport(loc.getX(), loc.getY(), loc.getZ());
                Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.AMBIENT, 0.4F, 1F, loc));
            }
        }
        return null;
    }
}
