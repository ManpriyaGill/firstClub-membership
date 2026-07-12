package com.firstclub.membership.enums;

/** Membership tiers in ascending order of privilege. Order of declaration = rank order. */
public enum TierLevel {
    SILVER,
    GOLD,
    PLATINUM;

    public boolean isHigherThan(TierLevel other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isLowerThan(TierLevel other) {
        return this.ordinal() < other.ordinal();
    }
}
