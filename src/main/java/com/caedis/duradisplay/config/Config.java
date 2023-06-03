package com.caedis.duradisplay.config;

import java.io.File;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.Loader;
import gregtech.GT_Mod;

public class Config {

    private static boolean configLoaded = false;
    public static boolean Enable = true;
    public static boolean HideBars = true;
    public static boolean TopLeft = false;
    public static boolean ShowPercentageWhenFull = false;

    public static Configuration config = null;

    public static void loadConfig() {
        if (configLoaded) {
            return;
        }
        configLoaded = true;
        final File configDir = new File(Launch.minecraftHome, "config");
        if (!configDir.isDirectory()) {
            configDir.mkdirs();
        }
        final File configFile = new File(configDir, "duradisplay.cfg");
        config = new Configuration(configFile);

        reloadConfigObject();

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void reloadConfigObject() {
        Enable = config
            .getBoolean("Enable", Configuration.CATEGORY_GENERAL, Enable, "Replace item bars with percentage");
        HideBars = config
            .getBoolean("HideBars", Configuration.CATEGORY_GENERAL, HideBars, "Hide durability/charge bars");
        TopLeft = config.getBoolean(
            "TopLeft",
            Configuration.CATEGORY_GENERAL,
            TopLeft,
            "Move percentage to top left instead of bottom centered ");
        ShowPercentageWhenFull = config.getBoolean(
            "ShowPercentageWhenFull",
            Configuration.CATEGORY_GENERAL,
            ShowPercentageWhenFull,
            "Show percentage when item is undamaged/full");

        if (config.hasChanged()) {
            config.save();
        }

        if (Loader.isModLoaded("gregtech")) {
            GT_Mod.gregtechproxy.mRenderItemDurabilityBar = GT_Mod.gregtechproxy.mRenderItemChargeBar = (Enable
                && HideBars) || true;
        }
    }
}
