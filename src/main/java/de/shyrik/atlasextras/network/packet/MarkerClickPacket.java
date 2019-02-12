package de.shyrik.atlasextras.network.packet;

import de.shyrik.atlasextras.features.travel.TravelHandler;
import de.shyrik.atlasextras.network.NetworkHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class MarkerClickPacket implements IMessage, IMessageHandler<MarkerClickPacket, IMessage> {

    public MarkerClickPacket() {
    }

    public MarkerClickPacket(int markerId, EntityPlayer player) {
        this.markerId = markerId;
        this.playerId = player.getUniqueID().toString();
    }

    private int markerId;
    private String playerId;

    @Override
    public void fromBytes(ByteBuf buf) {
        markerId = buf.readInt();
        playerId = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(markerId);
        ByteBufUtils.writeUTF8String(buf, playerId);
    }

    @Override
    public IMessage onMessage(MarkerClickPacket message, MessageContext ctx) {
        NetworkHelper.getThreadListener(ctx).addScheduledTask(() -> {
            TravelHandler.travel(NetworkHelper.getPlayerEntity(ctx).world, message.markerId, message.playerId);
        });
        return null;
    }
}
