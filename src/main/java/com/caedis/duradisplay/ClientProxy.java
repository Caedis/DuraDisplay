package com.caedis.duradisplay;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import gregtech.GT_Mod;

public class ClientProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        DuraDisplayConfig.synchronizeConfiguration(event.getSuggestedConfigurationFile());
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        // TODO: is this the proper way to override the config value?
        if (DuraDisplayConfig.Enable && DuraDisplayConfig.HideBars)
            GT_Mod.gregtechproxy.mRenderItemDurabilityBar = GT_Mod.gregtechproxy.mRenderItemChargeBar = false;
    }

}
