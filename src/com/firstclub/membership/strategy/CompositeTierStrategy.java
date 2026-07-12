package com.firstclub.membership.strategy;

import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.model.OrderStats;
import com.firstclub.membership.model.Tier;
import com.firstclub.membership.model.User;

import java.util.List;
import java.util.Map;

public class CompositeTierStrategy implements TierEvaluationStrategy {

    private final List<TierEvaluationStrategy> strategies;

    public CompositeTierStrategy(List<TierEvaluationStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public TierLevel evaluate(User user, OrderStats stats, Map<TierLevel, Tier> tierConfig) {
        TierLevel best = TierLevel.SILVER; // base tier everyone has
        for (TierEvaluationStrategy strategy : strategies) {
            TierLevel candidate = strategy.evaluate(user, stats, tierConfig);
            if (candidate != null && candidate.isHigherThan(best)) {
                best = candidate;
            }
        }
        return best;
    }
}
