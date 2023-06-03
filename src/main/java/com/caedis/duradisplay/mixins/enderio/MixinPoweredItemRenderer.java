package com.caedis.duradisplay.mixins.enderio;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.caedis.duradisplay.config.Config;

import crazypants.enderio.item.darksteel.PoweredItemRenderer;

@Mixin(value = PoweredItemRenderer.class, remap = false)
public abstract class MixinPoweredItemRenderer {

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
        method = "renderToInventory(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/RenderBlocks;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/RenderItem;renderItemIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IIZ)V",
            shift = At.Shift.AFTER),
        cancellable = true)
    private void renderToInventory(ItemStack stack, RenderBlocks renderBlocks, CallbackInfo callbackInfo) {
        if (Config.Enable && Config.HideBars) {
            callbackInfo.cancel();
        }
    }
}
