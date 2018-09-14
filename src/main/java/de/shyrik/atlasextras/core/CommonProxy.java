package de.shyrik.atlasextras.core;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.compat.WaystonesHandler;
import de.shyrik.atlasextras.features.travel.AtlasExtrasCostHandler;
import de.shyrik.atlasextras.compat.SignpostHandler;
import de.shyrik.atlasextras.features.travel.TravelHandler;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageEditWaystone;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy implements IModProxy {

    public void preInit(FMLPreInitializationEvent event) {
        TravelHandler.registerCostHandler(AtlasExtras.MODID, new AtlasExtrasCostHandler());

        if (Loader.isModLoaded(SignpostHandler.MODID) && Configuration.COMPAT.compatSignpost) {
            SignpostHandler sph = new SignpostHandler();
            MinecraftForge.EVENT_BUS.register(sph);
            TravelHandler.registerCostHandler(SignpostHandler.MODID, sph);
        }

        if (Loader.isModLoaded(WaystonesHandler.MODID) && Configuration.COMPAT.compatWaystone) {
            WaystonesHandler wsh = new WaystonesHandler();
            MinecraftForge.EVENT_BUS.register(wsh);
            TravelHandler.registerCostHandler(WaystonesHandler.MODID, wsh);
            NetworkHandler.channel.registerMessage(WaystonesHandler.class, MessageEditWaystone.class, 12, Side.SERVER);
        }
    }

    public void init(FMLInitializationEvent event) {

    }
}
