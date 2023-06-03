package com.caedis.duradisplay.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTUtils {

    public static GTToolsInfo getToolInfo(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        GTToolsInfo gti = new GTToolsInfo();

        // is spray can
        if (nbt.hasKey("GT.RemainingPaint")) {
            gti.setRemainingPaint(nbt.getLong("GT.RemainingPaint"));
        }

        if (nbt.hasKey("GT.ToolStats")) {
            NBTTagCompound ts = nbt.getCompoundTag("GT.ToolStats");
            gti.setDamage(ts.getLong("Damage"));
            gti.setMaxDamage(ts.getLong("MaxDamage"));
        }
        return gti;
    }

    public static long getNBTTagLong(ItemStack stack, String node, String... path) {
        NBTTagCompound aNBT = stack.getTagCompound();
        if (aNBT != null) {
            for (String tag : path) {
                NBTTagCompound newTag = aNBT.getCompoundTag(tag);
                if (newTag == null) return 0;
                aNBT = newTag;
            }
            return aNBT.getLong(node);
        }
        return 0;
    }

    public static int getNBTTagInt(ItemStack stack, String node, String... path) {
        NBTTagCompound aNBT = stack.getTagCompound();
        if (aNBT != null) {
            for (String tag : path) {
                NBTTagCompound newTag = aNBT.getCompoundTag(tag);
                if (newTag == null) return 0;
                aNBT = newTag;
            }
            return aNBT.getInteger(node);
        }
        return 0;
    }

    public static String getNBTTagString(ItemStack stack, String node, String... path) {
        NBTTagCompound aNBT = stack.getTagCompound();
        if (aNBT != null) {
            for (String tag : path) {
                NBTTagCompound newTag = aNBT.getCompoundTag(tag);
                if (newTag == null) return "";
                aNBT = newTag;
            }
            return aNBT.getString(node);
        }
        return "";
    }

}
