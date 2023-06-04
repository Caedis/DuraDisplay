package com.caedis.duradisplay.render;

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import com.caedis.duradisplay.config.Config;

public abstract class ItemStackOverlay {

    public boolean isFull = false;
    public int color;
    public String value;
    protected int x, y;

    public abstract int getColor();

    public abstract int getLocation();

    public void Render(FontRenderer fontRenderer, int xPosition, int yPosition, float zLevel) {
        int stringWidth = fontRenderer.getStringWidth(value);
        SetXY(xPosition, yPosition, stringWidth);
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glTranslatef(0, 0, zLevel + 1000);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        fontRenderer.drawString(value, x, y, getColor(), true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glPopMatrix();
    }

    private void SetXY(int xPosition, int yPosition, int stringWidth) {
        switch (getLocation()) {
            case 1 -> { // bottom left
                x = (xPosition * 2) + 2;
                y = (yPosition * 2) + 22;
            }
            default -> { // bottom center
                x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                y = (yPosition * 2) + 22;
            }
            case 3 -> { // bottom right
                x = (xPosition + 20) * 2 - stringWidth - 10;
                y = (yPosition * 2) + 22;
            }
            case 4 -> { // center left
                x = (xPosition * 2) + 2;
                y = (yPosition * 2) + 11;
            }
            case 5 -> { // center
                x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                y = (yPosition * 2) + 11;
            }
            case 6 -> { // center right
                x = (xPosition + 20) * 2 - stringWidth - 10;
                y = (yPosition * 2) + 11;
            }
            case 7 -> { // top left
                x = (xPosition * 2) + 2;
                y = (yPosition * 2) + 2;
            }
            case 8 -> { // top center
                x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                y = (yPosition * 2) + 2;
            }
            case 9 -> { // top right
                x = (xPosition + 20) * 2 - stringWidth - 10;
                y = (yPosition * 2) + 2;
            }
        }
    }

    public static class DurabilityOverlay extends ItemStackOverlay {

        @Override
        public void Render(FontRenderer fontRenderer, int xPosition, int yPosition, float zLevel) {
            if (!Config.Durability_PercentageWhenFull && this.isFull) return;
            super.Render(fontRenderer, xPosition, yPosition, zLevel);
        }

        @Override
        public int getColor() {
            return color;
        }

        @Override
        public int getLocation() {
            return Config.Durability_PercentageLocation;
        }
    }

    public static class ChargeOverlay extends ItemStackOverlay {

        @Override
        public void Render(FontRenderer fontRenderer, int xPosition, int yPosition, float zLevel) {
            if (!Config.Charge_PercentageWhenFull && this.isFull) return;
            super.Render(fontRenderer, xPosition, yPosition, zLevel);
        }

        @Override
        public int getColor() {
            return 0xFF55FFFF;
        }

        @Override
        public int getLocation() {
            return Config.Charge_PercentageLocation;
        }
    }
}
