package com.firstclub.membership.strategy;

import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.model.OrderStats;
import com.firstclub.membership.model.Tier;
import com.firstclub.membership.model.User;

import java.util.Map;


public interface TierEvaluationStrategy {

    TierLevel evaluate(User user, OrderStats stats, Map<TierLevel, Tier> tierConfig);
}
