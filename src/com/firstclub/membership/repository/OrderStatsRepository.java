package com.firstclub.membership.repository;

import com.firstclub.membership.model.OrderStats;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderStatsRepository {
    private final Map<Long, OrderStats> statsByUserId = new ConcurrentHashMap<>();

    public OrderStats getOrCreate(long userId) {
        return statsByUserId.computeIfAbsent(userId, OrderStats::new);
    }
}
