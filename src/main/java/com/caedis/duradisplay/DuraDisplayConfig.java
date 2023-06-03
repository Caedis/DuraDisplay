package com.caedis.duradisplay;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class DuraDisplayConfig {

    public static boolean Enable = true;
    public static boolean HideBars = true;
    public static boolean TopLeft = false;
    public static boolean ShowPercentageWhenFull = true;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        // greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I
        // greet?");
        Enable = configuration
            .getBoolean("Enable", Configuration.CATEGORY_GENERAL, Enable, "Replace item bars with percentage");
        HideBars = configuration
            .getBoolean("HideBars", Configuration.CATEGORY_GENERAL, HideBars, "Hide durability/charge bars");
        TopLeft = configuration.getBoolean(
            "TopLeft",
            Configuration.CATEGORY_GENERAL,
            TopLeft,
            "Move percentage to top left instead of bottom centered ");
        ShowPercentageWhenFull = configuration.getBoolean("ShowPercentageWhenFull", Configuration.CATEGORY_GENERAL, ShowPercentageWhenFull, "Show percentage when item is undamaged/full");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
