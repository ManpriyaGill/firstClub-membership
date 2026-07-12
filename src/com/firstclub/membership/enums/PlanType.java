package com.firstclub.membership.enums;

/** Supported membership plan durations. Extensible: add a new constant to support a new plan. */
public enum PlanType {
    MONTHLY(1),
    QUARTERLY(3),
    YEARLY(12);

    private final int durationInMonths;

    PlanType(int durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }
}
