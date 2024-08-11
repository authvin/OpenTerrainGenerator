package com.pg85.otg.util.gen;

import java.util.Random;

public class WorldHeight {
    /*
     * The minimum and maximum Y values for the world.
     * In modern minecraft, the max y is 320 but not inclusive, meaning max build height is 319.
     * The min y is -64, which is inclusive
     * This gives a combined height of 382 blocks, not including Y 320
     */
    private final int minY;
    // maxY here is *inclusive*, meaning 255 in old MC, 319 in new MC
    private final int maxY;

    public WorldHeight(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getHeight() {
        return maxY - minY;
    }

    public int getXAboveMin(int offset) {
        return offset + minY;
    }

    public int getXBelowMax(int offset) {
        return maxY - offset;
    }

    // Return a random height within X above the min height
    public int getXAboveMin(int bound, Random random) {
        return random.nextInt(bound) + minY;
    }

    public int getXBelowMax(int bound, Random random) {
        return maxY - random.nextInt(bound);
    }
}
