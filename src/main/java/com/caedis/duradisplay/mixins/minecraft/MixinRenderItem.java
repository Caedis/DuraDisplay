package com.caedis.duradisplay.mixins.minecraft;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.caedis.duradisplay.config.Config;
import com.caedis.duradisplay.render.DurabilityRenderer;

import gregtech.api.items.GT_MetaBase_Item;
import ic2.core.item.ItemIC2;

@Mixin(value = RenderItem.class)
public abstract class MixinRenderItem {

    // TODO: refactor all of this
    final Class[] ignoredOverlayClasses = new Class[] { ItemIC2.class, };

    @Shadow
    private float zLevel;

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(
        method = "renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/Item;showDurabilityBar(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean showDurabilityBar(Item item0, ItemStack stack0, FontRenderer fontRenderer,
        TextureManager textureManager, ItemStack stack, int xPosition, int yPosition, String string) {
        if (!Config.Enable) return item0.showDurabilityBar(stack0);

        DurabilityRenderer.RenderDurability(fontRenderer, stack0, xPosition, yPosition, zLevel);
        return !Config.HideBars && item0.showDurabilityBar(stack0);
    }

    // Handle GT Tools
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
        method = "renderItemAndEffectIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;II)V",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraftforge/client/ForgeHooksClient;renderInventoryItem(Lnet/minecraft/client/renderer/RenderBlocks;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;ZFFF)Z",
            ordinal = 0))
    private void renderItemAndEffectIntoGUI(FontRenderer fontRenderer, TextureManager textureManager, ItemStack stack,
        int xPosition, int yPosition, CallbackInfo ci) {
        if (!Config.Enable) return;
        if (stack == null || stack.getItem() == null || !(stack.getItem() instanceof GT_MetaBase_Item)) return;

        DurabilityRenderer.RenderDurability(fontRenderer, stack, xPosition, yPosition, zLevel);
    }
}
