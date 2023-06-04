package com.caedis.duradisplay.render;

import java.awt.*;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import com.caedis.duradisplay.config.Config;
import com.caedis.duradisplay.utils.GTToolsInfo;
import com.caedis.duradisplay.utils.NBTUtils;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.item.darksteel.IDarkSteelItem;
import gregtech.api.items.GT_MetaBase_Item;

public class DurabilityRenderer {

    private static final Map<Class<?>, Function<ItemStack, List<ItemStackOverlay>>> itemHandlers;
    private static final NumberFormat nf = NumberFormat.getNumberInstance();

    static {
        nf.setRoundingMode(RoundingMode.FLOOR);
        nf.setMaximumFractionDigits(0);

        itemHandlers = new LinkedHashMap<>();
        itemHandlers.put(GT_MetaBase_Item.class, DurabilityRenderer::handleGregTech);
        itemHandlers.put(IEnergyContainerItem.class, DurabilityRenderer::handleEnergyContainer);
        itemHandlers.put(IDarkSteelItem.class, DurabilityRenderer::handleDarkSteelItems);
        itemHandlers.put(Item.class, DurabilityRenderer::handleDefault);

    }

    public static void Render(FontRenderer fontRenderer, ItemStack stack, int xPosition, int yPosition, float zLevel) {
        List<ItemStackOverlay> overlays = new ArrayList<>();

        for (Map.Entry<Class<?>, Function<ItemStack, List<ItemStackOverlay>>> entry : itemHandlers.entrySet()) {
            if (entry.getKey()
                .isInstance(stack.getItem())) {
                List<ItemStackOverlay> list = entry.getValue()
                    .apply(stack);
                if (list != null) {
                    overlays.addAll(list);
                }
            }
        }

        for (ItemStackOverlay overlay : overlays) {
            overlay.Render(fontRenderer, xPosition, yPosition, zLevel);
        }
    }

    public static int getRGBDurabilityForDisplay(double dur) {
        return Color.HSBtoRGB(Math.max(0.0F, (float) dur) / 3.0F, 1.0F, 1.0F);
    }

    private static List<ItemStackOverlay> handleDefault(@NotNull ItemStack stack) {
        if (!Config.Durability_Enable
            || !(stack.isItemStackDamageable() && (Config.Durability_PercentageWhenFull || stack.isItemDamaged())))
            return null;
        assert stack.getItem() != null;

        List<ItemStackOverlay> overlays = new ArrayList<>();

        ItemStackOverlay durabilityOverlay = new ItemStackOverlay.DurabilityOverlay();
        double durability = (1 - stack.getItem()
            .getDurabilityForDisplay(stack));
        durabilityOverlay.color = getRGBDurabilityForDisplay(durability);
        durability *= 100;
        durabilityOverlay.isFull = durability == 100.0;
        durabilityOverlay.value = nf.format(durability) + "%";
        overlays.add(durabilityOverlay);

        return overlays;
    }

    private static List<ItemStackOverlay> handleGregTech(@NotNull ItemStack stack) {
        if (!stack.hasTagCompound()) return null;
        GT_MetaBase_Item gtItem = ((GT_MetaBase_Item) stack.getItem());
        assert gtItem != null;

        List<ItemStackOverlay> overlays = new ArrayList<>();

        if (Config.Charge_Enable) {
            Long[] elecStats = gtItem.getElectricStats(stack);
            if (elecStats != null) {
                ItemStackOverlay chargeOverlay = new ItemStackOverlay.ChargeOverlay();
                double charge = ((double) gtItem.getRealCharge(stack) / elecStats[0]) * 100;
                chargeOverlay.isFull = charge == 100.0;
                chargeOverlay.value = nf.format(charge) + "%";
                overlays.add(chargeOverlay);
            }
        }

        if (Config.Durability_Enable) {
            ItemStackOverlay durabilityOverlay = new ItemStackOverlay.DurabilityOverlay();
            GTToolsInfo gti = NBTUtils.getToolInfo(stack);
            if (gti.getRemainingPaint() > 0) {
                durabilityOverlay.color = 0xFFFFFF;
                durabilityOverlay.value = nf.format(gti.getRemainingPaint());
                durabilityOverlay.isFull = (double) gti.getRemainingPaint() / gti.getMaxPaint() == 100.0;
                overlays.add(durabilityOverlay);
            } else if (gti.getMaxDamage() > 0) {
                double durability = (1 - (double) gti.getDamage() / gti.getMaxDamage());
                durabilityOverlay.color = getRGBDurabilityForDisplay(durability);
                durability *= 100;
                durabilityOverlay.isFull = durability == 100.0;
                durabilityOverlay.value = nf.format(durability) + "%";
                overlays.add(durabilityOverlay);
            }
        }

        return overlays;
    }

    private static List<ItemStackOverlay> handleEnergyContainer(@NotNull ItemStack stack) {
        if (!Config.Charge_Enable || !(stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("Energy"))) return null; // because TiCon tools have the interface
        IEnergyContainerItem eci = ((IEnergyContainerItem) stack.getItem());
        assert eci != null;

        List<ItemStackOverlay> overlays = new ArrayList<>();

        ItemStackOverlay chargeOverlay = new ItemStackOverlay.ChargeOverlay();
        double durability = ((double) eci.getEnergyStored(stack) / eci.getMaxEnergyStored(stack)) * 100;
        chargeOverlay.isFull = durability == 100.0;
        chargeOverlay.value = nf.format(durability) + "%";
        overlays.add(chargeOverlay);

        // normal item durability is handled in default case

        return overlays;
    }

    // handles all other EIO items
    private static List<ItemStackOverlay> handleDarkSteelItems(@NotNull ItemStack stack) {
        if (!Config.Charge_Enable || !stack.hasTagCompound()) return null;

        List<ItemStackOverlay> overlays = new ArrayList<>();
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasKey("enderio.darksteel.upgrade.energyUpgrade")) {
            NBTTagCompound upgrade = nbt.getCompoundTag("enderio.darksteel.upgrade.energyUpgrade");
            int capacity = upgrade.getInteger("capacity");
            int energy = upgrade.getInteger("energy");

            ItemStackOverlay chargeOverlay = new ItemStackOverlay.ChargeOverlay();
            double durability = ((double) energy / capacity) * 100;
            chargeOverlay.isFull = durability == 100.0;
            chargeOverlay.value = nf.format(durability) + "%";

            overlays.add(chargeOverlay);
        }

        // normal item durability is handled in default case

        return overlays;
    }
}
