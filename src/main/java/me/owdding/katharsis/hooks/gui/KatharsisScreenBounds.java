package me.owdding.katharsis.hooks.gui;

import net.minecraft.client.gui.navigation.ScreenRectangle;

public class KatharsisScreenBounds {

    private int x;
    private int y;
    private int width;
    private int height;

    private ScreenRectangle bounds;

    public KatharsisScreenBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new ScreenRectangle(x, y, width, height);
    }

    public ScreenRectangle updateOrGet(int x, int y, int width, int height) {
        if (this.x != x || this.y != y || this.width != width || this.height != height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.bounds = new ScreenRectangle(x, y, width, height);
        }
        return this.bounds;
    }
}
