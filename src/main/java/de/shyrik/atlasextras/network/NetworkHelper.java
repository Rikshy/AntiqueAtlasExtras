package de.shyrik.atlasextras.network;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.network.packet.MarkerClickPacket;
import de.shyrik.atlasextras.network.packet.TravelEffectPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetworkHelper {
    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(AtlasExtras.CHANNEL);

    private static int packetId = 0;

    /**
     * Registers all packets and handlers - call this during {@link FMLPreInitializationEvent}
     */
    public static void registerPackets() {
        dispatcher.registerMessage(MarkerClickPacket.class, MarkerClickPacket.class, packetId++, Side.SERVER);
        dispatcher.registerMessage(TravelEffectPacket.class, TravelEffectPacket.class, packetId++, Side.CLIENT);
    }

    public static IThreadListener getThreadListener(MessageContext ctx) {
        return ctx.side == Side.SERVER ? (WorldServer) ctx.getServerHandler().player.world : getClientThreadListener();
    }

    @SideOnly(Side.CLIENT)
    public static IThreadListener getClientThreadListener() {
        return Minecraft.getMinecraft();
    }

    /**
     * Returns a side-appropriate EntityPlayer for use during message handling
     */
    public static EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }

    /**
     * Send this message to everyone.
     * See {@link SimpleNetworkWrapper#sendToAll(IMessage)}
     */
    public static void sendToAll(IMessage message) {
        dispatcher.sendToAll(message);
    }

    public static void sendToServer(IMessage message) {
        dispatcher.sendToServer(message);
    }

    public static void sendAround(IMessage message, BlockPos pos, int dimId) {
        dispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimId, pos.getX(), pos.getY(), pos.getZ(), 64));
    }
}
