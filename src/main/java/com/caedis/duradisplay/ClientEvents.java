package com.caedis.duradisplay;

import com.caedis.duradisplay.config.Config;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents {

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (event.modID.equals(Tags.MODID)) {
            Config.config.save();
            Config.reloadConfigObject();
        }
    }
}
