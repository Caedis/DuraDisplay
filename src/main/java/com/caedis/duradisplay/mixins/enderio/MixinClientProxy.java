package com.caedis.duradisplay.mixins.enderio;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.caedis.duradisplay.DuraDisplayConfig;

import crazypants.enderio.ClientProxy;
import crazypants.enderio.item.darksteel.DarkSteelItems;

@Mixin(value = ClientProxy.class, remap = false)
public abstract class MixinClientProxy {

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(
        method = "load()V",
        at = @At(
            value = "INVOKE",
            target = "Lcrazypants/enderio/item/darksteel/DarkSteelItems;registerItemRenderer()V"))
    private void RegisterItemRenderer() {
        if (!(DuraDisplayConfig.Enable || DuraDisplayConfig.HideBars)) {
            DarkSteelItems.registerItemRenderer();
        }
    }
}
