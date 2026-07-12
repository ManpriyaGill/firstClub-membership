package com.firstclub.membership.model;

import com.firstclub.membership.enums.PlanType;

import java.math.BigDecimal;

public final class MembershipPlan {
    private final PlanType type;
    private final BigDecimal price;
    private final int durationInDays;

    public MembershipPlan(PlanType type, BigDecimal price, int durationInDays) {
        this.type = type;
        this.price = price;
        this.durationInDays = durationInDays;
    }

    public PlanType getType() {
        return type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    @Override
    public String toString() {
        return type + " plan, price=" + price + ", duration=" + durationInDays + " days";
    }
}
