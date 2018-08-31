package de.shyrik.atlasextras.network;

import de.shyrik.atlasextras.AtlasExtras;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHelper {
    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(AtlasExtras.CHANNEL);

    private static int packetId = 0;

    /**
     *  Registers all packets and handlers - call this during {@link FMLPreInitializationEvent}
     */
    public static void registerPackets() {
        NetworkHelper.dispatcher.registerMessage(MarkerClickPacket.class, MarkerClickPacket.class, packetId++, Side.SERVER);
    }

    /**
     * Send this message to everyone.
     * See {@link SimpleNetworkWrapper#sendToAll(IMessage)}
     */
    public static void sendToAll(IMessage message) {
        NetworkHelper.dispatcher.sendToAll(message);
    }
    public static void sendToServer(IMessage message) {
        NetworkHelper.dispatcher.sendToServer(message);
    }
}
