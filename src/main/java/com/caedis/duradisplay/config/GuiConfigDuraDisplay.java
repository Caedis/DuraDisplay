package com.caedis.duradisplay.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import com.caedis.duradisplay.Tags;
import com.google.common.collect.Lists;

import cpw.mods.fml.client.config.GuiConfig;

@SuppressWarnings("unused")
public class GuiConfigDuraDisplay extends GuiConfig {

    public GuiConfigDuraDisplay(GuiScreen parent) {
        super(
            parent,
            Lists.newArrayList(new ConfigElement<>(Config.config.getCategory(Configuration.CATEGORY_GENERAL))),
            Tags.MODID,
            "general",
            false,
            false,
            getAbridgedConfigPath(Config.config.toString()));
    }
}
