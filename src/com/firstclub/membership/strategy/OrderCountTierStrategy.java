package com.firstclub.membership.strategy;

import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.model.OrderStats;
import com.firstclub.membership.model.Tier;
import com.firstclub.membership.model.User;

import java.util.Map;

public class OrderCountTierStrategy implements TierEvaluationStrategy {

    @Override
    public TierLevel evaluate(User user, OrderStats stats, Map<TierLevel, Tier> tierConfig) {
        TierLevel best = null;
        int orderCount = stats.getOrderCountThisMonth();
        for (Tier tier : tierConfig.values()) {
            if (orderCount >= tier.getCriteria().getMinOrderCount()) {
                if (best == null || tier.getLevel().isHigherThan(best)) {
                    best = tier.getLevel();
                }
            }
        }
        return best;
    }
}
