package de.shyrik.atlasextras.network;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.features.travel.TravelHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class MarkerClickPacket implements IMessage, IMessageHandler<MarkerClickPacket, IMessage> {

    public MarkerClickPacket() {
    }

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
        NetworkHelper.getThreadListener(ctx).addScheduledTask(() -> {
            TravelHandler.travel(AtlasExtras.proxy.getPlayerEntity(ctx).world, message.markderId, message.playerId);
        });
        return null;
    }
}
