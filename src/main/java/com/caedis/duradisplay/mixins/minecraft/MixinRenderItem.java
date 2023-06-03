package com.caedis.duradisplay.mixins.minecraft;

import java.awt.*;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Arrays;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.caedis.duradisplay.DuraDisplayConfig;
import com.caedis.duradisplay.utils.GTToolsInfo;
import com.caedis.duradisplay.utils.NBTUtils;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.item.darksteel.IDarkSteelItem;
import gregtech.api.items.GT_MetaBase_Item;
import ic2.core.item.ItemIC2;

@Mixin(value = RenderItem.class)
public abstract class MixinRenderItem {

    // TODO: refactor all of this

    private static NumberFormat nf = NumberFormat.getNumberInstance();

    static {
        nf.setRoundingMode(RoundingMode.FLOOR);
        nf.setMaximumFractionDigits(0);
    }

    final Class[] ignoredOverlayClasses = new Class[]{ItemIC2.class,};

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
        if (item0 == null) return false;
        if (!DuraDisplayConfig.Enable) return item0.showDurabilityBar(stack0);
        if (Arrays.stream(ignoredOverlayClasses)
            .anyMatch(c -> c.isInstance(item0))) return item0.showDurabilityBar(stack0);

        RenderDurabilityText(fontRenderer, stack0, xPosition, yPosition);
        return !DuraDisplayConfig.HideBars && item0.showDurabilityBar(stack0);
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
        if (!DuraDisplayConfig.Enable) return;
        if (stack == null || stack.getItem() == null || !(stack.getItem() instanceof GT_MetaBase_Item)) return;
        RenderDurabilityText(fontRenderer, stack, xPosition, yPosition);
    }

    private void RenderDurabilityText(FontRenderer fontRenderer, ItemStack stack, int xPosition, int yPosition) {

        double durability = 0;
        int color = 0;
        String durString = "0";

        Item item = stack.getItem();

        // GT Tools are very special
        if (item instanceof GT_MetaBase_Item gtItem) {
            if (!stack.hasTagCompound()) return;

            Long[] elecStats = gtItem.getElectricStats(stack);

            if (elecStats != null) {
                durability = ((double) gtItem.getRealCharge(stack) / elecStats[0]) * 100;
                color = 0xFF55FFFF;
                durString = nf.format(durability) + "%";
            } else {
                GTToolsInfo gti = NBTUtils.getToolInfo(stack);
                if (gti.getRemainingPaint() > 0) {
                    durability = gti.getRemainingPaint();
                    color = 0xFFFFFFFF;
                    durString = nf.format(durability);
                } else {
                    if (gti.getMaxDamage() == 0) return;
                    durability = (1 - (double) gti.getDamage() / gti.getMaxDamage());
                    color = getRGBDurabilityForDisplay(durability);
                    durability *= 100;
                    durString = nf.format(durability) + "%";
                }
            }
        } else if (item instanceof IEnergyContainerItem && stack.hasTagCompound()
            && stack.getTagCompound()
            .hasKey("Energy")) {
            IEnergyContainerItem eci = ((IEnergyContainerItem) item);
            color = 0xFF55FFFF;
            durability = ((double) eci.getEnergyStored(stack) / eci.getMaxEnergyStored(stack)) * 100;
            durString = nf.format(durability) + "%";
        } else if (item instanceof IDarkSteelItem) {
            if (!stack.hasTagCompound()) return;
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("enderio.darksteel.upgrade.energyUpgrade")) {
                NBTTagCompound upgrade = nbt.getCompoundTag("enderio.darksteel.upgrade.energyUpgrade");
                int capacity = upgrade.getInteger("capacity");
                int energy = upgrade.getInteger("energy");
                durability = ((double) energy / capacity) * 100;
                durString = nf.format(durability) + "%";
                color = 0xFF55FFFF;
            } else return;
        } else if (DuraDisplayConfig.ShowPercentageWhenFull || stack.isItemDamaged()) {
            durability = (1 - item.getDurabilityForDisplay(stack));
            color = getRGBDurabilityForDisplay(durability);
            durability *= 100;
            durString = nf.format(durability) + "%";
        } else { // not a GT tool nor a damaged item
            return;
        }

        int x, y;
        if (DuraDisplayConfig.TopLeft) {
            x = (xPosition * 2) + 2;
            y = (yPosition * 2) + 2;
        } else {
            int stringWidth = fontRenderer.getStringWidth(durString);
            x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
            y = (yPosition * 2) + 22;
        }
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glTranslatef(0, 0, zLevel + 1000);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        fontRenderer.drawString(durString, x, y, color, true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glPopMatrix();
    }

    public int getRGBDurabilityForDisplay(double dur) {

        return Color.HSBtoRGB(Math.max(0.0F, (float) dur) / 3.0F, 1.0F, 1.0F);
    }
}
