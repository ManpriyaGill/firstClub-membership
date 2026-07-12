package com.firstclub.membership.model;

import com.firstclub.membership.enums.BenefitType;

public final class Benefit {
    private final BenefitType type;
    private final String description;
    private final double value;

    public Benefit(BenefitType type, String description, double value) {
        this.type = type;
        this.description = description;
        this.value = value;
    }

    public BenefitType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + (value > 0 ? "(" + value + ")" : "") + " - " + description;
    }
}
