package com.firstclub.membership.model;

import com.firstclub.membership.enums.TierLevel;

import java.util.Collections;
import java.util.List;

public final class Tier {
    private final TierLevel level;
    private final List<Benefit> benefits;
    private final TierCriteria criteria;

    public Tier(TierLevel level, List<Benefit> benefits, TierCriteria criteria) {
        this.level = level;
        this.benefits = Collections.unmodifiableList(benefits);
        this.criteria = criteria;
    }

    public TierLevel getLevel() {
        return level;
    }

    public List<Benefit> getBenefits() {
        return benefits;
    }

    public TierCriteria getCriteria() {
        return criteria;
    }
}
