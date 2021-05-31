package de.shyrik.atlasextras.network.packet;

import de.shyrik.atlasextras.features.travel.AtlasHandler;
import de.shyrik.atlasextras.network.NetworkHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateMarkerPacket implements IMessage, IMessageHandler<UpdateMarkerPacket, IMessage> {

    private int dimId;
    private String name;
    private BlockPos pos;
    private String modId;

    private boolean canTravelTo;
    private boolean canTravelFrom;

    public UpdateMarkerPacket() {
    }

    public UpdateMarkerPacket(int dimId, BlockPos pos, String name, boolean canTravelTo, boolean canTravelFrom, String modId) {
        this.dimId = dimId;
        this.pos = pos;
        this.name = name;
        this.modId = modId;

        this.canTravelFrom = canTravelFrom;
        this.canTravelTo = canTravelTo;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimId = buf.readInt();
        pos = BlockPos.of(buf.readLong());
        name = ByteBufUtils.getContentDump(buf);
        modId = ByteBufUtils.getContentDump(buf);

        canTravelFrom = buf.readBoolean();
        canTravelTo = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimId);
        buf.writeLong(pos.asLong());
        buf.write
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, modId);

        buf.writeBoolean(canTravelFrom);
        buf.writeBoolean(canTravelTo);
    }

    @Override
    public IMessage onMessage(UpdateMarkerPacket message, MessageContext ctx) {
        NetworkHelper.getThreadListener(ctx).addScheduledTask(() -> {
            WorldServer w = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimId);
            AtlasHandler.addMarker(w, message.pos, message.name, message.canTravelTo, message.canTravelFrom, message.modId);
        });

        return null;
    }
}