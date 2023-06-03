package com.caedis.duradisplay.render;

import java.awt.*;
import java.math.RoundingMode;
import java.text.NumberFormat;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

import com.caedis.duradisplay.config.Config;
import com.caedis.duradisplay.utils.GTToolsInfo;
import com.caedis.duradisplay.utils.NBTUtils;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.item.darksteel.IDarkSteelItem;
import gregtech.api.items.GT_MetaBase_Item;

public class DurabilityRenderer {

    private static NumberFormat nf = NumberFormat.getNumberInstance();

    static {
        nf.setRoundingMode(RoundingMode.FLOOR);
        nf.setMaximumFractionDigits(0);
    }

    public static void RenderDurability(FontRenderer fontRenderer, ItemStack stack, int xPosition, int yPosition,
        float zLevel) {

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
        } else if (item instanceof IEnergyContainerItem eci && stack.hasTagCompound()
            && stack.getTagCompound()
                .hasKey("Energy")) {
                    color = 0xFF55FFFF;
                    durability = ((double) eci.getEnergyStored(stack) / eci.getMaxEnergyStored(stack)) * 100;
                    durString = nf.format(durability) + "%";
                } else
            if (item instanceof IDarkSteelItem) {
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
            } else if (stack.isItemStackDamageable() && (Config.ShowPercentageWhenFull || stack.isItemDamaged())) {
                durability = (1 - item.getDurabilityForDisplay(stack));
                color = getRGBDurabilityForDisplay(durability);
                durability *= 100;
                durString = nf.format(durability) + "%";
            } else { // not a GT tool nor a damaged item
                return;
            }

        int x, y;
        if (Config.TopLeft) {
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

    public static int getRGBDurabilityForDisplay(double dur) {
        return Color.HSBtoRGB(Math.max(0.0F, (float) dur) / 3.0F, 1.0F, 1.0F);
    }
}
