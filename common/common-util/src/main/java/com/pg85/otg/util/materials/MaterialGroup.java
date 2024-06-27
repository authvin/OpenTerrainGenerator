package com.pg85.otg.util.materials;

import lombok.Getter;

/**
 * A MaterialGroup is an enum that represents the special values that can be present in a MaterialSet
 */
@Getter
public enum MaterialGroup {
    ALL_MATERIALS("All"), // ALL Materials
    SOLID_MATERIALS("Solid"), // Solid Materials
    NON_SOLID_MATERIALS("NonSolid"), // Non-Solid Materials
    LIQUIDS("Liquid"),; // Liquid Materials

    private final String keyword;

    MaterialGroup(String keyword) {
        this.keyword = keyword;
    }
    public static MaterialGroup ofKeyword(String keyword) {
        for (MaterialGroup group : values()) {
            if (group.getKeyword().equalsIgnoreCase(keyword)) {
                return group;
            }
        }
        return null;
    }

    boolean contains(LocalMaterialData material) {
        return switch (this) {
            case ALL_MATERIALS -> true;
            case SOLID_MATERIALS -> material.isSolid();
            case NON_SOLID_MATERIALS -> !material.isSolid();
            case LIQUIDS -> material.isLiquid();
        };
    }
}
