package com.caedis.duradisplay.core;

import static com.caedis.duradisplay.mixins.TargetedMod.*;
import static java.nio.file.Files.walk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.launchwrapper.Launch;

import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.caedis.duradisplay.DuraDisplay;
import com.caedis.duradisplay.mixins.*;
import com.gtnewhorizon.gtnhmixins.MinecraftURLClassPath;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1005)
@IFMLLoadingPlugin.DependsOn("cofh.asm.LoadingPlugin")
public class DuraDisplayCore implements IMixinConfigPlugin {

    private static final Path MODS_DIRECTORY_PATH = new File(Launch.minecraftHome, "mods/").toPath();

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    // This method return a List<String> of mixins. Every mixins in this list will be loaded.
    @Override
    public List<String> getMixins() {
        final boolean isDevelopmentEnvironment = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        List<TargetedMod> loadedMods = Arrays.stream(TargetedMod.values())
            .filter(mod -> mod == VANILLA || (mod.loadInDevelopment && isDevelopmentEnvironment) || loadJarOf(mod))
            .collect(Collectors.toList());

        for (TargetedMod mod : TargetedMod.values()) {
            if (loadedMods.contains(mod)) {
                DuraDisplay.LOG.info("Found " + mod.modName + "! Integrating now...");
            } else {
                DuraDisplay.LOG.info("Could not find " + mod.modName + "! Skipping integration....");
            }
        }

        List<String> mixins = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.shouldLoad(loadedMods)) {
                mixins.add(mixin.mixinClass);
                DuraDisplay.LOG.debug("Loading mixin: " + mixin.mixinClass);
            }
        }
        return mixins;
    }

    private boolean loadJarOf(final TargetedMod mod) {
        try {
            File jar = findJarOf(mod);
            if (jar == null) {
                DuraDisplay.LOG.info("Jar not found for " + mod);
                return false;
            }

            DuraDisplay.LOG.info("Attempting to add " + jar + " to the URL Class Path");
            if (!jar.exists()) {
                throw new FileNotFoundException(jar.toString());
            }
            MinecraftURLClassPath.addJar(jar);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File findJarOf(final TargetedMod mod) {
        try {
            return walk(MODS_DIRECTORY_PATH).filter(mod::isMatchingJar)
                .map(Path::toFile)
                .findFirst()
                .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

}
