package com.firstclub.membership.strategy;

import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.model.OrderStats;
import com.firstclub.membership.model.Tier;
import com.firstclub.membership.model.User;

import java.util.Map;

public class OrderValueTierStrategy implements TierEvaluationStrategy {

    @Override
    public TierLevel evaluate(User user, OrderStats stats, Map<TierLevel, Tier> tierConfig) {
        TierLevel best = null;
        double totalValue = stats.getTotalOrderValueThisMonth();
        for (Tier tier : tierConfig.values()) {
            if (totalValue >= tier.getCriteria().getMinMonthlyOrderValue()) {
                if (best == null || tier.getLevel().isHigherThan(best)) {
                    best = tier.getLevel();
                }
            }
        }
        return best;
    }
}
