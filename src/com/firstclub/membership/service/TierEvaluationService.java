package com.firstclub.membership.service;

import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.factory.TierFactory;
import com.firstclub.membership.model.OrderStats;
import com.firstclub.membership.model.User;
import com.firstclub.membership.strategy.TierEvaluationStrategy;

public class TierEvaluationService {

    private final TierFactory tierFactory;
    private final TierEvaluationStrategy strategy;

    public TierEvaluationService(TierFactory tierFactory, TierEvaluationStrategy strategy) {
        this.tierFactory = tierFactory;
        this.strategy = strategy;
    }

    public TierLevel evaluate(User user, OrderStats stats) {
        return strategy.evaluate(user, stats, tierFactory.getAllTiers());
    }
}
