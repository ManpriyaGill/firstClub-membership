package com.firstclub.membership.model;

import java.util.Collections;
import java.util.Set;

public final class TierCriteria {
    private final int minOrderCount;          // e.g. more than X orders
    private final double minMonthlyOrderValue; // total order value in a month
    private final Set<String> eligibleCohorts; // e.g. "EMPLOYEE", "INFLUENCER"

    public TierCriteria(int minOrderCount, double minMonthlyOrderValue, Set<String> eligibleCohorts) {
        this.minOrderCount = minOrderCount;
        this.minMonthlyOrderValue = minMonthlyOrderValue;
        this.eligibleCohorts = eligibleCohorts == null ? Collections.emptySet() : eligibleCohorts;
    }

    public int getMinOrderCount() {
        return minOrderCount;
    }

    public double getMinMonthlyOrderValue() {
        return minMonthlyOrderValue;
    }

    public Set<String> getEligibleCohorts() {
        return eligibleCohorts;
    }
}
