package com.caedis.duradisplay.utils;

public class GTToolsInfo {

    // spray cans
    private long RemainingPaint;
    private final long MaxPaint = 512;
    private boolean isElectric;
    private long MaxCharge;
    private long Charge;
    private long MaxDamage;
    private long Damage;

    public boolean isElectric() {
        return isElectric;
    }

    public long getMaxCharge() {
        return MaxCharge;
    }

    public long getCharge() {
        return Charge;
    }

    public long getMaxDamage() {
        return MaxDamage;
    }

    public long getDamage() {
        return Damage;
    }

    public long getRemainingPaint() {
        return RemainingPaint;
    }

    public void setRemainingPaint(long remainingPaint) {
        RemainingPaint = remainingPaint;
    }

    public void setElectric(boolean electric) {
        isElectric = electric;
    }

    public void setMaxCharge(long maxCharge) {
        MaxCharge = maxCharge;
    }

    public void setCharge(long charge) {
        Charge = charge;
    }

    public void setMaxDamage(long maxDamage) {
        MaxDamage = maxDamage;
    }

    public void setDamage(long damage) {
        Damage = damage;
    }

    public long getMaxPaint() {
        return MaxPaint;
    }
}
