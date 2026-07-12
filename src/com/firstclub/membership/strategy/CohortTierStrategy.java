package com.firstclub.membership.strategy;

import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.model.OrderStats;
import com.firstclub.membership.model.Tier;
import com.firstclub.membership.model.User;

import java.util.Map;

public class CohortTierStrategy implements TierEvaluationStrategy {

    @Override
    public TierLevel evaluate(User user, OrderStats stats, Map<TierLevel, Tier> tierConfig) {
        if (user.getCohort() == null) {
            return null;
        }
        TierLevel best = null;
        for (Tier tier : tierConfig.values()) {
            if (tier.getCriteria().getEligibleCohorts().contains(user.getCohort())) {
                if (best == null || tier.getLevel().isHigherThan(best)) {
                    best = tier.getLevel();
                }
            }
        }
        return best;
    }
}
