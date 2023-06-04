package com.caedis.duradisplay.config;

import java.io.File;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.Loader;
import gregtech.GT_Mod;

public class Config {

    public static final String CATEGORY_CHARGE = "charge";
    public static final String CATEGORY_DURABILITY = "durability";

    private static boolean configLoaded = false;
    public static boolean Durability_Enable = true;
    public static boolean Charge_Enable = true;

    public static boolean Durability_HideBar = true;
    public static boolean Charge_HideBar = true;
    public static int Durability_PercentageLocation = 2;
    public static int Charge_PercentageLocation = 8;
    public static boolean Durability_PercentageWhenFull = false;
    public static boolean Charge_PercentageWhenFull = false;

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
        Durability_Enable = config
            .getBoolean("Durability_Enable", Config.CATEGORY_DURABILITY, Durability_Enable, "Enable durability module");

        Durability_HideBar = config
            .getBoolean("Durability_HideBar", Config.CATEGORY_DURABILITY, Durability_HideBar, "Hide durability bar");

        Charge_Enable = config
            .getBoolean("Charge_Enable", Config.CATEGORY_CHARGE, Charge_Enable, "Enable charge module");

        Charge_HideBar = config.getBoolean("Charge_HideBar", Config.CATEGORY_CHARGE, Charge_HideBar, "Hide charge bar");

        Durability_PercentageLocation = config.getInt(
            "Durability_PercentageLocation",
            Config.CATEGORY_DURABILITY,
            Durability_PercentageLocation,
            1,
            9,
            "Location in item where the durability percentage will be (numpad style)");

        Charge_PercentageLocation = config.getInt(
            "Charge_PercentageLocation",
            Config.CATEGORY_CHARGE,
            Charge_PercentageLocation,
            1,
            9,
            "Location in item where the charge percentage will be (numpad style)");

        Durability_PercentageWhenFull = config.getBoolean(
            "Durability_PercentageWhenFull",
            Config.CATEGORY_DURABILITY,
            Durability_PercentageWhenFull,
            "Show durability percentage when item is undamaged/full");

        Charge_PercentageWhenFull = config.getBoolean(
            "Charge_PercentageWhenFull",
            Config.CATEGORY_CHARGE,
            Charge_PercentageWhenFull,
            "Show charge percentage when item is full");

        if (config.hasChanged()) {
            config.save();
        }

        if (Loader.isModLoaded("gregtech")) {
            GT_Mod.gregtechproxy.mRenderItemDurabilityBar = !(Durability_Enable && Durability_HideBar);
            GT_Mod.gregtechproxy.mRenderItemChargeBar = !(Charge_Enable && Charge_HideBar);
        }
    }
}
