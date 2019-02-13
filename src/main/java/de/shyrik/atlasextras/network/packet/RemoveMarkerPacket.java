package de.shyrik.atlasextras.network.packet;

import de.shyrik.atlasextras.features.travel.AtlasHandler;
import de.shyrik.atlasextras.network.NetworkHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RemoveMarkerPacket implements IMessage, IMessageHandler<RemoveMarkerPacket, IMessage> {

    private int dimId;
    private BlockPos pos;

    public RemoveMarkerPacket() {
    }

    public RemoveMarkerPacket(int dimId, BlockPos pos) {
        this.dimId = dimId;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimId = buf.readInt();
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimId);
        buf.writeLong(pos.toLong());
    }

    @Override
    public IMessage onMessage(RemoveMarkerPacket message, MessageContext ctx) {
        NetworkHelper.getThreadListener(ctx).addScheduledTask(() -> {
            WorldServer w = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimId);
            AtlasHandler.removeMarker(w, message.pos);
        });

        return null;
    }
}