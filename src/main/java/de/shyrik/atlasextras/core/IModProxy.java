package de.shyrik.atlasextras.core;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IModProxy {

    void preInit(FMLPreInitializationEvent event);
    void init(FMLInitializationEvent event);
}
